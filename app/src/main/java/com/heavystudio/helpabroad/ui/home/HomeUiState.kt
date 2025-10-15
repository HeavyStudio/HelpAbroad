package com.heavystudio.helpabroad.ui.home

import com.heavystudio.helpabroad.data.local.dto.CountryListItem
import com.heavystudio.helpabroad.ui.model.UiCountryDetails

data class HomeUiState(
    val searchQuery: String = "",
    val searchResults: List<CountryListItem> = emptyList(),
    val isSearchResultsVisible: Boolean = false,
    val selectedCountryDetails: UiCountryDetails? = null,
    val isLoading: Boolean = false,
    val isDirectCallEnabled: Boolean = false,
    val isConfirmBeforeCallEnabled: Boolean = true,
    val numberToCallForConfirmation: String? = null
)
