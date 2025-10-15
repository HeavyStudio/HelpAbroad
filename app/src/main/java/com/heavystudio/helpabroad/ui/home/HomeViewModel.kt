package com.heavystudio.helpabroad.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.core.AppConfig
import com.heavystudio.helpabroad.data.local.dto.CountryDetails
import com.heavystudio.helpabroad.data.settings.SettingsRepository
import com.heavystudio.helpabroad.domain.repository.CountryRepository
import com.heavystudio.helpabroad.ui.model.UiCountryDetails
import com.heavystudio.helpabroad.ui.model.UiEmergencyService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CountryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _effectiveLangCode: String

    init {
        val deviceLang = Locale.getDefault().language
        val supportedLanguages = AppConfig.supportedLanguages
        _effectiveLangCode = if (deviceLang in supportedLanguages) deviceLang else "en"

        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .flatMapLatest { query ->
                    if (query.length < 2) flowOf(emptyList())
                    else repository.searchCountries(query, _effectiveLangCode)
                }
                .collect { results ->
                    _uiState.update {
                        it.copy(
                            searchResults = results,
                            isSearchResultsVisible = results.isNotEmpty() && _searchQuery.value.isNotBlank() && it.selectedCountryDetails == null
                        )
                    }
                }
        }

        // Ce bloc gère UNIQUEMENT les réglages. Il est sûr.
        viewModelScope.launch {
            settingsRepository.directCallFlow.collect { isEnabled ->
                _uiState.update { it.copy(isDirectCallEnabled = isEnabled) }
            }
        }
        viewModelScope.launch {
            settingsRepository.confirmBeforeCallFlow.collect { isEnabled ->
                _uiState.update { it.copy(isConfirmBeforeCallEnabled = isEnabled) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _uiState.update {
            it.copy(
                searchQuery = query,
                selectedCountryDetails = null
            )
        }
    }

    fun onCountrySelected(countryId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isSearchResultsVisible = false) }
            repository.getCountryDetails(countryId).firstOrNull()?.let { details ->
                val uiDetails = mapToUiModel(details)
                _uiState.update {
                    it.copy(
                        searchQuery = "",
                        selectedCountryDetails = uiDetails,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onEmergencyNumberClicked(number: String) {
        if (uiState.value.isDirectCallEnabled && uiState.value.isConfirmBeforeCallEnabled) {
            _uiState.update { it.copy(numberToCallForConfirmation = number) }
        }
    }

    fun onCallConfirmationDismissed() {
        _uiState.update { it.copy(numberToCallForConfirmation = null) }
    }

    private fun mapToUiModel(details: CountryDetails): UiCountryDetails {
        val countryName = details.names.find { it.languageCode == _effectiveLangCode }?.name
            ?: details.names.find { it.languageCode == "en" }?.name
            ?: details.country.isoCode
        val countryIsoCode = details.country.isoCode
        val services = details.services.mapNotNull { serviceDetails ->
            val serviceName = serviceDetails.names.find { it.languageCode == _effectiveLangCode }?.name
                ?: serviceDetails.names.find { it.languageCode == "en" }?.name
            if (serviceName != null) UiEmergencyService(
                code = serviceDetails.type.serviceCode,
                name = serviceName,
                number = serviceDetails.number.phoneNumber
            ) else null
        }
        return UiCountryDetails(
            countryName = countryName,
            countryIsoCode = countryIsoCode,
            services = services
        )
    }
}