package com.heavystudio.helpabroad.ui.main

import com.heavystudio.helpabroad.data.local.dto.CountryListItem

data class UiEmergencyService(
    val code: String,
    val name: String,
    val number: String
)

data class UiCountryDetails(
    val countryName: String,
    val services: List<UiEmergencyService>
)

data class MainUiState(
    val searchQuery: String = "",
    val searchResults: List<CountryListItem> = emptyList(),
    val allCountries: List<CountryListItem> = emptyList(),
    val isSearchResultsVisible: Boolean = false,
    val selectedCountryDetails: UiCountryDetails? = null,
    val isLoading: Boolean = false,

    val isDirectCallEnabled: Boolean = false,
    val isConfirmBeforeCallEnabled: Boolean = true,
    val numberToCallForConfirmation: String? = null
)
