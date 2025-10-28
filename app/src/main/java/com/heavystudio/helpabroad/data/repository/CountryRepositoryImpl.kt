package com.heavystudio.helpabroad.data.repository

import com.heavystudio.helpabroad.data.local.dao.CountryDao
import com.heavystudio.helpabroad.data.local.dto.CountryDetails
import com.heavystudio.helpabroad.data.local.dto.CountryListItem
import com.heavystudio.helpabroad.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Concrete implementation of the [CountryRepository] interface.
 * This class is responsible for interacting with the local data source ([CountryDao])
 * to fetch country-related data. It provides the data as reactive streams (Flow).
 *
 * @property countryDao The Data Access Object for countries, injected via Hilt.
 * @constructor Creates an instance of CountryRepositoryImpl.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the implementation.
 */
class CountryRepositoryImpl @Inject constructor(
    private val countryDao: CountryDao
) : CountryRepository {

    override fun searchCountries(query: String, langCode: String): Flow<List<CountryListItem>> {
        return countryDao.searchCountries(query, langCode)
    }

    override fun getAllCountries(langCode: String): Flow<List<CountryListItem>> {
        return countryDao.getAllCountries(langCode)
    }

    override fun getCountryDetails(countryId: Int): Flow<CountryDetails?> {
        return countryDao.getCountryDetails(countryId)
    }
}