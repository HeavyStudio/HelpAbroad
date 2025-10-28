package com.heavystudio.helpabroad.ui.home

import com.heavystudio.helpabroad.data.local.dto.CountryListItem
import com.heavystudio.helpabroad.ui.model.UiCountryDetails

/**
 * Represents the UI state for the Home screen.
 *
 * This data class holds all the necessary information to render the Home UI,
 * including search state, results, selected country details, and loading status.
 *
 * @property searchQuery The current text entered by the user in the search bar.
 * @property searchResults The list of countries matching the current search query.
 * @property isSearchResultsVisible A boolean flag to determine if the search results list should
 *           be displayed.
 * @property selectedCountryDetails The details of the country currently selected by the user.
 *           Null if no country is selected.
 * @property isLoading A boolean flag indicating whether a data loading operation (e.g., fetching
 *           country details) is in progress.
 *
 * @author Heavy Studio
 * @since 0.1.0 Creation of HomeUiState.
 */
data class HomeUiState(
    val searchQuery: String = "",
    val searchResults: List<CountryListItem> = emptyList(),
    val isSearchResultsVisible: Boolean = false,
    val selectedCountryDetails: UiCountryDetails? = null,
    val isLoading: Boolean = false
)
