package com.heavystudio.helpabroad.data.repository

import com.heavystudio.helpabroad.data.dao.CountryDao
import com.heavystudio.helpabroad.data.database.CountryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CountryRepositoryImpl @Inject constructor(
    private val countryDao: CountryDao
) : CountryRepository {

    override suspend fun insertCountry(country: CountryEntity): Long {
        return countryDao.insertCountry(country)
    }

    override suspend fun getCountryByIsoCode(countryIsoCode: String): CountryEntity? {
        return countryDao.getCountryByIsoCode(countryIsoCode)
    }

    override fun getAllCountries(): Flow<List<CountryEntity>> {
        return countryDao.getAllCountries()
    }

    override suspend fun updateCountry(country: CountryEntity) {
        return countryDao.updateCountry(country)
    }

    override suspend fun deleteCountryByIsoCode(countryIsoCode: String) {
        return countryDao.deleteCountryByIsoCode(countryIsoCode)
    }

    override suspend fun deleteAllCountries() {
        return countryDao.deleteAllCountries()
    }
}