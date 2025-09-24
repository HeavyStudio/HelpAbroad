package com.heavystudio.helpabroad.data.repository

import com.heavystudio.helpabroad.data.local.dao.CountryDao
import com.heavystudio.helpabroad.data.local.dto.CountryDetails
import com.heavystudio.helpabroad.data.local.dto.CountryListItem
import com.heavystudio.helpabroad.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

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