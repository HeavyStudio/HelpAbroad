package com.heavystudio.helpabroad.ui.model

/**
 * Represents the detailed information of a country, formatted for display in the UI.
 *
 * This data class holds all necessary details for a specific country, including its
 * identifier, name, and a list of available emergency services.
 *
 * @param countryIsoCode The ISO 3166-1 alpha-2 code of the country (e.g., "US", "CA").
 * @param countryName The common name of the country (e.g., "United States", "Canada").
 * @param services A list of [UiEmergencyService] objects available in this country.
 *
 * @author Heavy Studio.
 */
data class UiCountryDetails(
    val countryIsoCode: String,
    val countryName: String,
    val services: List<UiEmergencyService>
)

/**
 * Represents a single emergency service for display in the UI.
 *
 * @property code A unique identifier for the service type (e.g., "ambulance", "police").
 * @property name The display name of the emergency service (e.g., "Ambulance").
 * @property number The phone number for the emergency service.
 *
 * @author Heavy Studio.
 */
data class UiEmergencyService(
    val code: String,
    val name: String,
    val number: String
)