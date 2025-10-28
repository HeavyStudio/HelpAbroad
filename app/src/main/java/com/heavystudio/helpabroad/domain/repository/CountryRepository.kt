package com.heavystudio.helpabroad.domain.repository

import com.heavystudio.helpabroad.data.local.dto.CountryDetails
import com.heavystudio.helpabroad.data.local.dto.CountryListItem
import kotlinx.coroutines.flow.Flow

/**
 * Interface for accessing country data.
 * This repository provides methods for fetching lists of countries and details for a specific country.
 * Implementations of this interface are responsible for the data source (e.g., local database, network API).
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the repository.
 */
interface CountryRepository {
    fun searchCountries(query: String, langCode: String): Flow<List<CountryListItem>>
    fun getAllCountries(langCode: String): Flow<List<CountryListItem>>
    fun getCountryDetails(countryId: Int): Flow<CountryDetails?>
    fun getCountriesByIds(countryIds: List<Int>, langCode: String): Flow<List<CountryListItem>>
}