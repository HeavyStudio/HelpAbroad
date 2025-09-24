package com.heavystudio.helpabroad.domain.repository

import com.heavystudio.helpabroad.data.local.dto.CountryDetails
import com.heavystudio.helpabroad.data.local.dto.CountryListItem
import kotlinx.coroutines.flow.Flow

interface CountryRepository {
    fun searchCountries(query: String, langCode: String): Flow<List<CountryListItem>>
    fun getAllCountries(langCode: String): Flow<List<CountryListItem>>
    fun getCountryDetails(countryId: Int): Flow<CountryDetails?>
}