package com.heavystudio.helpabroad.ui.countries

import com.heavystudio.helpabroad.data.local.dto.CountryListItem

data class CountriesUiState(
    val allCountries: List<CountryListItem> = emptyList(),
    val isLoading: Boolean = true
)
