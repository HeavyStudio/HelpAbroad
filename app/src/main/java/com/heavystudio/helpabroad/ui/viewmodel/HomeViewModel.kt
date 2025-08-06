package com.heavystudio.helpabroad.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.data.location.LocationManager
import com.heavystudio.helpabroad.data.location.LocationResultWrapper
import com.heavystudio.helpabroad.data.repository.CountryRepository
import com.heavystudio.helpabroad.ui.viewmodel.state.LocationActionRequired
import com.heavystudio.helpabroad.ui.viewmodel.state.LocationUiState
import com.heavystudio.helpabroad.utils.getStreetNameFromCoordinates
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val locationManager: LocationManager,
    private val countryRepository: CountryRepository
) : ViewModel() {

    private val _locationUiState = MutableStateFlow(LocationUiState())
    val locationUiState: StateFlow<LocationUiState> = _locationUiState.asStateFlow()

    init {
        if (locationManager.hasLocationPermission()) {
            fetchCurrentLocation()
        } else {
            // TODO
        }
    }

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            _locationUiState.value = _locationUiState.value.copy(
                isLoading = true,
                errorMessage = null,
                requiresAction = null,
                countryName = null
            )

            when (val result = locationManager.getCurrentLocation()) {
                is LocationResultWrapper.Success -> {
                    val location = result.location
                    val countryCode = locationManager.getCountryCodeFromLocation(location)

                    var countryName: String? = null
                    var flagEmoji: String? = null

                    val street = getStreetNameFromCoordinates(
                        appContext,
                        location.latitude,
                        location.longitude
                    )

                    if (countryCode != null && countryCode.length == 2) {
                        val deviceLocale = Locale.getDefault()
                        val countryLocale = Locale("", countryCode.uppercase())
                        countryName = countryLocale.getDisplayCountry(deviceLocale)

                        val countryEntityFromDb = countryRepository
                            .getCountryByIsoCode(countryCode)
                            .firstOrNull()

                        flagEmoji = countryEntityFromDb?.flagEmoji

                        // Fallback
                        if (countryName.isNullOrEmpty() ||
                            countryName.equals(countryCode, ignoreCase = true)) {
                            countryName = countryEntityFromDb?.name

                            if (countryName.isNullOrEmpty()) {
                                countryName = countryCode
                            }
                        }
                    } else if (countryCode != null) {
                        countryName = countryCode
                    }

                    Log.d("HomeViewModel", "Fetched location:\n" +
                            " -- latitude: ${location.latitude}\n" +
                            " -- longitutde: ${location.longitude}\n" +
                            " -- countryName: $countryName\n" +
                            " -- flagEmoji: $flagEmoji")
                    _locationUiState.value = _locationUiState.value.copy(
                        isLoading = false,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        countryCode = countryCode,
                        countryName = countryName,
                        countryFlagEmoji = flagEmoji,
                        street = street
                    )
                }
                is LocationResultWrapper.NoPermission -> {
                    _locationUiState.value = _locationUiState.value.copy(
                        isLoading = false,
                        errorMessage = "Location permission not granted.",
                        requiresAction = LocationActionRequired.NO_PERMISSION
                    )
                }
                is LocationResultWrapper.SettingsNotOptimal -> {
                    _locationUiState.value = _locationUiState.value.copy(
                        isLoading = false,
                        errorMessage = "Location settings not optimal.",
                        requiresAction = LocationActionRequired.SETTINGS_NOT_OPTIMAL
                    )
                }
                is LocationResultWrapper.Error -> {
                    _locationUiState.value = _locationUiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error fetching location: ${result.exception.message}"
                    )
                }
            }
        }
    }

    fun locationSettingsOptimalFlowHandled() {
        _locationUiState.value = _locationUiState.value.copy(
            requiresAction = null,
            errorMessage = null
        )
        fetchCurrentLocation()
    }
}