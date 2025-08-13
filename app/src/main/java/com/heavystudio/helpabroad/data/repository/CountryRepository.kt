package com.heavystudio.helpabroad.data.repository

import com.heavystudio.helpabroad.data.dao.CountryDao
import com.heavystudio.helpabroad.data.database.CountryEntity
import com.heavystudio.helpabroad.utils.catchAndLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountryRepository @Inject constructor(
    private val countryDao: CountryDao
) {

    private val tag = "CountryRepository"

    fun getCountryByIsoCode(isoCode: String): Flow<CountryEntity?> {
        return countryDao.getCountryByIsoCode(isoCode)
            .catchAndLog(tag, "Error fetching country by ISO code: $isoCode", null)
    }

    fun getAllCountries(): Flow<List<CountryEntity>> {
        return countryDao.getAllCountries()
            .catchAndLog(tag, "Error fetching all countries", emptyList())
    }

    fun getCountriesMember112(): Flow<List<CountryEntity>> {
        return countryDao.get112Countries()
            .catchAndLog(tag, "Error fetching countries members of 112 dispatch", emptyList())
    }

    fun getCountriesMember911(): Flow<List<CountryEntity>> {
        return countryDao.get911Countries()
            .catchAndLog(tag, "Error fetching countries members of 911 dispatch", emptyList())
    }
}