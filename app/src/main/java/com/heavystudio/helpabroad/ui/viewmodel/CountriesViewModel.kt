package com.heavystudio.helpabroad.ui.viewmodel

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.data.repository.CountryRepository
import com.heavystudio.helpabroad.data.repository.EmergencyNumberRepository // <<< CORRECT REPOSITORY
import com.heavystudio.helpabroad.data.database.CountryEntity
import com.heavystudio.helpabroad.data.database.EmergencyNumberEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountriesViewModel @Inject constructor(
    private val countryRepository: CountryRepository,
    private val emergencyNumberRepository: EmergencyNumberRepository // <<< INJECT CORRECT REPOSITORY
) : ViewModel() {

    private val _tag = "CountriesViewModel"

    private val _allCountries = MutableStateFlow<List<CountryEntity>>(emptyList())
    val allCountries: StateFlow<List<CountryEntity>> = _allCountries.asStateFlow()

    private val _selectedCountry = MutableStateFlow<CountryEntity?>(null)
    val selectedCountry: StateFlow<CountryEntity?> = _selectedCountry.asStateFlow()

    private val _currentEmergencyNumbers = MutableStateFlow<List<EmergencyNumberEntity>>(emptyList())
    val currentEmergencyNumbers: StateFlow<List<EmergencyNumberEntity>> = _currentEmergencyNumbers.asStateFlow()

    private val _isLoadingCountries = MutableStateFlow(false)
    val isLoadingCountries: StateFlow<Boolean> = _isLoadingCountries.asStateFlow()

    private val _isLoadingNumbers = MutableStateFlow(false)
    val isLoadingNumbers: StateFlow<Boolean> = _isLoadingNumbers.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadAllCountries()
        observeSelectedCountryForNumbers()
    }

    private fun loadAllCountries() {
        viewModelScope.launch {
            _isLoadingCountries.value = true
            _errorMessage.value = null
            countryRepository.getAllCountries() // Correctly from CountryRepository
                .catch { exception ->
                    Log.e(_tag, "Error fetching all countries", exception)
                    _allCountries.value = emptyList()
                    _errorMessage.value = "Erreur lors du chargement des pays."
                    _isLoadingCountries.value = false
                }
                .collect { countries ->
                    _allCountries.value = countries
                    _isLoadingCountries.value = false
                    Log.d(_tag, "Loaded ${countries.size} countries.")
                }
        }
    }

    private fun observeSelectedCountryForNumbers() {
        viewModelScope.launch {
            selectedCountry.collectLatest { country ->
                if (country != null) {
                    _isLoadingNumbers.value = true
                    _errorMessage.value = null
                    Log.d(_tag, "Selected country changed to: ${country.isoCode}. Fetching numbers.")
                    // VVVVVV  CALLING THE CORRECT REPOSITORY VVVVVV
                    emergencyNumberRepository.getEmergencyNumbersByCountry(country.isoCode)
                        .catch { exception ->
                            Log.e(_tag, "Error fetching numbers for ${country.isoCode}", exception)
                            _currentEmergencyNumbers.value = emptyList()
                            _errorMessage.value = "Erreur lors du chargement des numéros d'urgence."
                            _isLoadingNumbers.value = false
                        }
                        .collect { numbers ->
                            _currentEmergencyNumbers.value = numbers
                            _isLoadingNumbers.value = false
                            Log.d(_tag, "Numbers for ${country.isoCode}: $numbers")
                        }
                } else {
                    _currentEmergencyNumbers.value = emptyList()
                    _isLoadingNumbers.value = false
                    Log.d(_tag, "Selected country cleared. Numbers cleared.")
                }
            }
        }
    }

    fun selectCountry(country: CountryEntity) {
        if (_selectedCountry.value?.isoCode != country.isoCode) {
            _selectedCountry.value = country
        } else {
            Log.d(_tag, "Country ${country.isoCode} is already selected.")
        }
    }

    fun clearSelectedCountry() {
        _selectedCountry.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
