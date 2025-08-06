package com.heavystudio.helpabroad.ui.viewmodel.state

data class LocationUiState(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val countryCode: String? = null,
    val countryName: String? = null,
    val countryFlagEmoji: String? = null,
    val street: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val requiresAction: LocationActionRequired? = null
)

enum class LocationActionRequired {
    SETTINGS_NOT_OPTIMAL,
    NO_PERMISSION
}
