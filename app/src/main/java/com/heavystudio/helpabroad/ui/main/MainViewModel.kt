package com.heavystudio.helpabroad.ui.main

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.data.local.dto.CountryDetails
import com.heavystudio.helpabroad.data.settings.SettingsRepository
import com.heavystudio.helpabroad.domain.repository.CountryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
class MainViewModel @Inject constructor(
    private val repository: CountryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val countriesListState = LazyListState()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _currentLangCode = Locale.getDefault().language
    private var _effectiveLangCode: String = "en"

    init {
        val deviceLang = _currentLangCode
        val supportedLangs = listOf("en", "fr", "es", "de", "it", "pt")
        _effectiveLangCode = if (deviceLang in supportedLangs) deviceLang else "en"
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .flatMapLatest { query ->
                    if (query.length < 2) {
                        Log.d("SEARCH_DEBUG", "Query too short, skipping search.")
                        flowOf(emptyList())
                    } else {
                        Log.d("SEARCH_DEBUG", "ViewModel: Launching search for query: '$query'.")
                        repository.searchCountries(query, _effectiveLangCode)
                    }
                }
                .collect { results ->
                    _uiState.update {
                        Log.d("SEARCH_DEBUG", "ViewModel: Received ${results.size} results from repository.")
                        it.copy(
                            searchResults = results,
                            isSearchResultsVisible = results.isNotEmpty() && _searchQuery.value.isNotBlank()
                        )
                    }
                }
        }

        viewModelScope.launch {
            combine(
                settingsRepository.directCallFlow,
                settingsRepository.confirmBeforeCallFlow
            ) { isDirectCall, isConfirm ->
                Pair(isDirectCall, isConfirm)
            }.collect { (isDirectCall, isConfirm) ->
                _uiState.update {
                    it.copy(
                        isDirectCallEnabled = isDirectCall,
                        isConfirmBeforeCallEnabled = isConfirm
                    )
                }
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            Log.i("LANG", "Effective language code: $_effectiveLangCode")
            repository.getAllCountries(langCode = _effectiveLangCode)
                .collect { countries ->
                    _uiState.update {
                        it.copy(
                            allCountries = countries,
                            isLoading = false
                        )
                    }
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
            // TODO: For testing, remove for prod
            settingsRepository.setDefaultCountryId(countryId)

            _uiState.update {
                it.copy(
                    isSearchResultsVisible = false,
                    isLoading = true
                )
            }

            repository.getCountryDetails(countryId).firstOrNull()?.let { details ->
                val uiDetails = mapToUiModel(details)
                _uiState.update {
                    it.copy(
                        searchQuery = "",
                        selectedCountryDetails = uiDetails,
                        isLoading = false
                    )
                }
                _searchQuery.value = ""
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
        val countryName = uiState.value.allCountries
            .find { it.countryId == details.country.id }?.name ?: details.country.isoCode
        val countryIsoCode = details.country.isoCode

        val services = details.services.mapNotNull { serviceDetails ->
            val serviceName = serviceDetails.names
                .find { it.languageCode == _effectiveLangCode }?.name

            if (serviceName != null) {
                UiEmergencyService(
                    code = serviceDetails.type.serviceCode,
                    name = serviceName,
                    number = serviceDetails.number.phoneNumber
                )
            } else {
                null
            }
        }

        return UiCountryDetails(countryName = countryName, countryIsoCode = countryIsoCode, services = services)
    }
}