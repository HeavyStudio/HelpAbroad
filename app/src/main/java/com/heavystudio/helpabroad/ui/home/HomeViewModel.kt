package com.heavystudio.helpabroad.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.data.location.LocationRepository
import com.heavystudio.helpabroad.data.repository.CountryRepository
import com.heavystudio.helpabroad.data.repository.EmergencyNumberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    @param:ApplicationContext private val context: Context,
    private val countryRepository: CountryRepository,
    private val emergencyNumberRepository: EmergencyNumberRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "ViewModel initialized, fetching initial data")
        fetchUserLocationAndCountryDetails()
    }

    fun fetchUserLocationAndCountryDetails() {
        Log.d(TAG, "fetchUserLocationAndCountryDetails called.")
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isAddressLoading = true,
                    userAddress = "Fetching location data...", // TODO: I18n
                    countryName = null,
                    countryFlag = null,
                    errorMessage = null
                )
            }

            locationRepository.getCurrentLocationData()
                .onStart {
                    Log.d(TAG, "getCurrentLocationData Flow started.")
                    _uiState.update {
                        it.copy(
                            isAddressLoading = true,
                            userAddress = "Fetching location data..." // TODO: I18n
                        )
                    }
                }
                .catch { e ->
                    Log.e(TAG, "Error fetching location data: ${e.message}", e)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAddressLoading = false,
                            // TODO: I18n
                            userAddress = "Could not retrieve location.",
                            errorMessage = "Error: ${e.message ?: "Unknown location error"}"
                        )
                    }
                }
                .collectLatest { locationData ->
                    Log.d(TAG, "LocationData collected: $locationData")

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAddressLoading = false,
                            userAddress = locationData.fullAddress,
                        )
                    }

                    if (locationData.countryCode != null) {
                        fetchCountryDetailsByIsoCode(locationData.countryCode)
                    } else {
                        Log.w(TAG, "No ISO code available from location data to " +
                                "fetch country details.")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                countryName = locationData.countryName ?: "Country not determined",
                                countryFlag = "❓",
                                errorMessage = if (locationData.countryName == null) {
                                    "Could not determine country from location." //TODO: I18n
                                } else null
                            )
                        }
                    }
                }
        }
    }

    fun refreshAllData() {
        Log.d(TAG, "refreshAllData called.")
        fetchUserLocationAndCountryDetails()
    }

    private fun fetchCountryDetailsByIsoCode(isoCode: String) {
        Log.d(TAG, "fetchCountryDetailsByIsoCode called for ISO: $isoCode")
        viewModelScope.launch {
            countryRepository.getCountryByIsoCode(isoCode)
                .onStart {
                    Log.d(TAG, "getCountryByIsoCode Flow started for: $isoCode")
                }
                .collectLatest { countryEntity ->
                    if (countryEntity != null) {
                        Log.d(TAG, "CountryEntity collected: ${countryEntity.name}")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                countryName = countryEntity.name,
                                countryFlag = countryEntity.flagEmoji ?: "\uD83C\uDFF3\uFE0F",
                                errorMessage = null
                            )
                        }
                        fetchEmergencyNumbers(isoCode)
                    } else {
                        Log.w(TAG, "No CountryEntity found or error for ISO code: $isoCode")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                countryName = uiState.value.countryName ?: "Country data not found",
                                countryFlag = "❓",
                                errorMessage = "Could not find detailed data for the detected " +
                                        "country.",
                                emergencyNumbers = emptyList()
                            )
                        }
                    }
                }
        }
    }

    private fun fetchEmergencyNumbers(isoCode: String) {
        Log.d(TAG, "fetchEmergencyNumbers called for ISO: $isoCode")
        viewModelScope.launch {
            _uiState.update {
                it.copy(areEmergencyNumbersLoading = true)
            }

            emergencyNumberRepository.getDisplayableEmergencyNumbersForCountry(isoCode)
                .catch { e ->
                    Log.e(TAG, "Error collecting emergency numbers for $isoCode: " +
                            "${e.message}")

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            areEmergencyNumbersLoading = false,
                            emergencyNumbers = emptyList(),
                            errorMessage = (it.errorMessage ?: "") +
                                "\nCould not load emergency numbers."
                        )
                    }
                }
                .collectLatest { numbers ->
                    Log.d(TAG, "Emergency numbers collected for $isoCode: $numbers")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            areEmergencyNumbersLoading = false,
                            emergencyNumbers = numbers
                        )
                    }
                }
        }
    }

    companion object {
        private const val TAG = "HomeVM"
    }


}