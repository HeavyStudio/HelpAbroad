package com.heavystudio.helpabroad.ui.main

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

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _currentLangCode = Locale.getDefault().language
    private val _isLoading = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .flatMapLatest { query ->
                    if (query.length < 2) {
                        flowOf(emptyList())
                    } else {
                        repository.searchCountries(query, _currentLangCode)
                    }
                }
                .collect { results ->
                    _uiState.update {
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
                _uiState.update {
                    it.copy(
                        isDirectCallEnabled = isDirectCall,
                        isConfirmBeforeCallEnabled = isConfirm
                    )
                }
            }
        }

        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllCountries(langCode = _currentLangCode)
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
                        searchQuery = uiDetails.countryName,
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

    fun onCallConfirmed() {
        val number = uiState.value.numberToCallForConfirmation
        if (number != null) {
            onCallConfirmationDismissed()
        }
    }

    fun onCallConfirmationDismissed() {
        _uiState.update { it.copy(numberToCallForConfirmation = null) }
    }

    private fun mapToUiModel(details: CountryDetails): UiCountryDetails {
        val countryName = uiState.value.allCountries
            .find { it.countryId == details.country.id }?.name ?: details.country.isoCode

        val services = details.services.mapNotNull { serviceDetails ->
            val serviceName = serviceDetails.names
                .find { it.languageCode == _currentLangCode }?.name

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

        return UiCountryDetails(countryName = countryName, services = services)
    }
}