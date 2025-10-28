package com.heavystudio.helpabroad.ui.countries

import com.heavystudio.helpabroad.data.local.dto.CountryListItem

/**
 * Represents the UI state for the countries screen.
 *
 * @param allCountries The list of countries to be displayed. Defaults to an empty list.
 * @param isLoading A boolean flag indicating if the country data is currently being loaded.
 *        Defaults to true.
 *
 * @author Heavy Studio
 * @since 0.1.0 Creation of the data class.
 */
data class CountriesUiState(
    val allCountries: List<CountryListItem> = emptyList(),
    val isLoading: Boolean = true
)
