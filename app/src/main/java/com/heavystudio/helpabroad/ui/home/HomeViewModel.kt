package com.heavystudio.helpabroad.ui.home

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.data.location.LocationRepository
import com.heavystudio.helpabroad.data.repository.CountryRepository
import com.heavystudio.helpabroad.data.repository.EmergencyNumberRepository
import com.heavystudio.helpabroad.data.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(isLoading = true, isAddressLoading = true)
        }
        loadUserAddress()
    }

    fun refreshAddress() {
        loadUserAddress()
    }

    private fun loadUserAddress() {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isAddressLoading = true,
                userAddress = "Fetching address..."
            ) }

            try {
                // 1. Get current location
                val location = locationRepository.tryGetQuickLocation(timeoutMillis = 5000L)

                location?.let { loc ->
                    // 2. Geocode location to an address
                    val geocoder = Geocoder(context)
                    var currentAddress = "Address not found"

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // Android 13+ (API 33+)
                        geocoder.getFromLocation(
                            loc.latitude, loc.longitude, 1
                        ) { addresses ->
                            if (addresses.isNotEmpty()) {
                                currentAddress = formatAddress(addresses[0])
                            }
                            _uiState.update {
                                it.copy(
                                    userAddress = currentAddress,
                                    isAddressLoading = false,
                                    isLoading = false
                                )
                            }
                        }
                    } else {
                        // For older versions
                        @Suppress("DEPRECATION")
                        try {
                            val addresses: List<Address>? = geocoder.getFromLocation(
                                loc.latitude, loc.longitude, 1
                            )
                            if (!addresses.isNullOrEmpty()) {
                                currentAddress = formatAddress(addresses[0])
                            }
                        } catch (e: IOException) {
                            Log.e("HomeVM", "Geocoder IOE", e)
                            currentAddress = "Service not available to get address"
                        }
                        _uiState.update {
                            it.copy(
                                userAddress = currentAddress,
                                isAddressLoading = false,
                                isLoading = false
                            )
                        }
                    }
                } ?: run {
                    _uiState.update {
                        it.copy(
                            userAddress = "Could not determine location",
                            isAddressLoading = false,
                            isLoading = false
                        )
                    }
                }
            } catch (e: SecurityException) {
                Log.e("HomeVM", "Location permission missing for address", e)
                _uiState.update {
                    it.copy(
                        userAddress = "Location permission denied",
                        isAddressLoading = false,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeVM", "Error getting location/address", e)
                _uiState.update {
                    it.copy(
                        userAddress = "Error finding address",
                        isAddressLoading = false,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun formatAddress(address: Address): String {
        val addressLines = (0..address.maxAddressLineIndex)
            .mapNotNull { address.getAddressLine(it) }

        return addressLines.joinToString(separator = ", ")
    }
}