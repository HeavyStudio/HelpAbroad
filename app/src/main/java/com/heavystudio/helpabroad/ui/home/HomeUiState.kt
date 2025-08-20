package com.heavystudio.helpabroad.ui.home

data class HomeUiState(
    val isLoading: Boolean = true,
    val userAddress: String = "Fetching address...",
    val isAddressLoading: Boolean = true,
    val countryName: String? = null,
    val countryFlag: String? = null
)
