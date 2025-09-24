package com.heavystudio.helpabroad.data.repository

import com.heavystudio.helpabroad.data.dao.CountryDao
import com.heavystudio.helpabroad.data.model.result.CountryWithLocalizedName
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CountryRepository @Inject constructor(
    private val countryDao: CountryDao
){

    fun getCountriesByLanguage(languageCode: String): Flow<List<CountryWithLocalizedName>> {
        return countryDao.getCountriesByLanguage(languageCode)
    }


    fun searchCountries(query: String, languageCode: String): Flow<List<CountryWithLocalizedName>> {
        return countryDao.searchCountries(query, languageCode)
    }
}