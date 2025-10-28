package com.heavystudio.helpabroad.data.local.dto

/**
 * Represents a simplified country data object, typically used for displaying in lists.
 * This data class is a lightweight representation of a country, containing only the essential
 * information needed for UI lists, such as a dropdown or a RecyclerView.
 *
 * @property countryId The unique identifier for the country.
 * @property isoCode The ISO 3166-1 alpha-2 code of the country (e.g., "US", "DE").
 * @property name The common name of the country (e.g., "United States", "Germany").
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the data class.
 */
data class CountryListItem(
    val countryId: Int,
    val isoCode: String,
    val name: String
)
