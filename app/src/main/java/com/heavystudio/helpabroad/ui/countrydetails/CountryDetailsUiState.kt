package com.heavystudio.helpabroad.ui.countrydetails

import com.heavystudio.helpabroad.ui.model.UiCountryDetails

/**
 * Represents the UI state for the Country Details screen.
 *
 * @property countryDetails The details of the country to be displayed. Null if not yet loaded.
 * @property isLoading True if the country details are currently being loaded, false otherwise.
 * @property isDirectCallEnabled True if the "direct call" feature is enabled in user settings.
 * @property isConfirmBeforeCallEnabled True if the "confirm before call" feature is enabled in user settings.
 * @property numberToConfirmBeforeCall The phone number that requires confirmation before dialing. This is used
 * to show a confirmation dialog to the user. Null if no call confirmation is pending.
 *
 * @author Heavy Studio.
 * @since 0.2.0 Creation of the data class.
 */
data class CountryDetailsUiState(
    val countryDetails: UiCountryDetails? = null,
    val isLoading: Boolean = false,
    val isDirectCallEnabled: Boolean = false,
    val isConfirmBeforeCallEnabled: Boolean = true,
    val numberToConfirmBeforeCall: String? = null
)
