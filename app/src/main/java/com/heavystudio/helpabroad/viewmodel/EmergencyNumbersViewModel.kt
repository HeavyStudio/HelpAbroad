package com.heavystudio.helpabroad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.dao.EmergencyNumberDao
import com.heavystudio.helpabroad.data.Country
import com.heavystudio.helpabroad.data.EmergencyNumber
import com.heavystudio.helpabroad.data.source.EuropeanCountriesData
import com.heavystudio.helpabroad.database.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EmergencyNumbersViewModel(application: Application) : AndroidViewModel(application) {

    private val emergencyNumberDao: EmergencyNumberDao

    init {
        val database = AppDatabase.getDatabase(application)
        emergencyNumberDao = database.emergencyNumberDao()

        viewModelScope.launch {
            initializeCountriesIfEmpty()
        }
    }

    // Retrieve all emergency numbers for a given country
    fun getEmergencyNumbersForCountry(countryCode: String): Flow<List<EmergencyNumber>> {
        return emergencyNumberDao.getEmergencyNumbersByCountryCode(countryCode)
    }

    // Retrieve the list of all countries (downloaded or not)
    fun getAllCountries(): Flow<List<Country>> {
        return emergencyNumberDao.getAllCountries()
    }

    // Retrieve the list of downloaded countries
    fun getDownloadedCountries(): Flow<List<Country>> {
        return emergencyNumberDao.getDownloadedCountries()
    }

    // Insert emergency numbers (for downloading)
    fun insertEmergencyNumbers(numbers: List<EmergencyNumber>) {
        viewModelScope.launch {
            emergencyNumberDao.insertEmergencyNumbers(numbers)
        }
    }

    // Check if a country is downloaded
    suspend fun hasCountryData(countryCode: String): Boolean {
        return emergencyNumberDao.countEmergencyNumbersForCountry(countryCode) > 0
    }

    // Delete emergency numbers from a country
    fun deleteEmergencyNumbersForCountry(countryCode: String) {
        viewModelScope.launch {
            emergencyNumberDao.deleteEmergencyNumbersByCountryCode(countryCode)
        }
    }

    fun downloadCountryData(country: Country, numbers: List<EmergencyNumber>) {
        viewModelScope.launch {
            emergencyNumberDao.downloadCountryData(country, numbers)
        }
    }

    fun deleteCountryDataAndNumbers(countryCode: String) {
        viewModelScope.launch {
            emergencyNumberDao.deleteCountryDataAndNumbers(countryCode)
        }
    }

    private suspend fun initializeCountriesIfEmpty() {
        val count = emergencyNumberDao.countAllCountries()
        if (count == 0) {
            val countriesToInsert = EuropeanCountriesData.getEuropeanCountriesForInitialLoad()
            emergencyNumberDao.insertCountries(countriesToInsert)
        }
    }
}