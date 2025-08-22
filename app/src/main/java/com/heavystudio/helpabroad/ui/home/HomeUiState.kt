package com.heavystudio.helpabroad.ui.home

import com.heavystudio.helpabroad.data.database.EmergencyNumberEntity

data class HomeUiState(
    // General
    val isLoading: Boolean = true,

    // Location
    val userAddress: String = "Fetching address...",
    val isAddressLoading: Boolean = true,
    val countryName: String? = null,
    val countryFlag: String? = null,
    val errorMessage: String? = null,

    // Emergency Numbers
    val emergencyNumbers: List<DisplayableEmergencyNumber> = emptyList(),
    val areEmergencyNumbersLoading: Boolean = false
)
