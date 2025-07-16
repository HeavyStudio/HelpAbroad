package com.heavystudio.helpabroad.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.data.model.EmergencyContact
import com.heavystudio.helpabroad.data.repository.EmergencyRepository
import com.heavystudio.helpabroad.data.source.system.TelephonyEmergencySource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class EmergencyNumbersUiState(
    val isLoading: Boolean = true,
    val emergencyContacts: List<EmergencyContact> = emptyList(),
    val errorMessage: String? = null,
    val permissionRequiredMessage: String? = null,
    val detectedCountry: String? = null,
    val detectedCountryIso: String? = null
)

@HiltViewModel
class EmergencyNumbersViewModel @Inject constructor(
    private val emergencyRepository: EmergencyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmergencyNumbersUiState())
    val uiState: StateFlow<EmergencyNumbersUiState> = _uiState.asStateFlow()

    fun initialLoadOrRefresh(hasPermission: Boolean) {
        Log.d(TAG, "initialLoadOrRefresh called. Has permission: $hasPermission")
        if (!hasPermission) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    permissionRequiredMessage = "READ_PHONE_STATE permission is required to load emergency numbers.",
                    emergencyContacts = emptyList(),
                    errorMessage = null
                )
            }
            return
        }
        // If permission is granted, clear any previous permission message and proceed to fetch
        _uiState.update { it.copy(permissionRequiredMessage = null) }
        fetchDetailedEmergencyContacts()
    }

    fun refreshEmergencyNumbers() {
        Log.d(TAG, "Refresh triggered for emergency contacts.")
        fetchDetailedEmergencyContacts()
    }

    private fun fetchDetailedEmergencyContacts() {
        viewModelScope.launch {
            Log.d(TAG, "fetchDetailedEmergencyContacts: Starting to collect from repository.")
            try {
                emergencyRepository.getEmergencyContact()
                    .onStart {
                        Log.d(TAG, "Flow collection started. Updating UI to loading.")
                        _uiState.update {
                            it.copy(
                                isLoading = true,
                                errorMessage = null,
                                permissionRequiredMessage = null,
                                detectedCountry = null
                            )
                        }
                    }
                    .catch { exception ->
                        Log.e(TAG, "Error collectif emergency contacts from repository", exception)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Failed to load numbers: ${exception.localizedMessage ?: "Unknown error"}",
                                detectedCountry = _uiState.value.detectedCountry
                            )
                        }
                    }
                    .collect { contacts ->
                        Log.d(TAG, "Collected emergency contacts: Count = ${contacts.size}")

                        val currentCountryIso = emergencyRepository.getCurrentNetworkCountryIso()
                        val displayCountryName = if (currentCountryIso != null) {
                            mapIsoToDisplayName(currentCountryIso)
                        } else {
                            if (contacts.isNotEmpty()) "Using general fallback numbers" else null
                        }

                        val finalErrorMessage = if (
                            contacts.isEmpty() &&
                            _uiState.value.errorMessage == null &&
                            _uiState.value.permissionRequiredMessage == null
                        ) {
                            "No emergency numbers found for your region."
                        } else {
                            _uiState.value.errorMessage
                        }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                emergencyContacts = contacts,
                                errorMessage = finalErrorMessage,
                                detectedCountry = displayCountryName,
                                detectedCountryIso = currentCountryIso
                            )
                        }
                    }
            } catch (se: SecurityException) {
                Log.e(TAG, "SecurityException directly from repository call", se)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        permissionRequiredMessage = "Permission wa denied or revoked",
                        errorMessage = null,
                        emergencyContacts = emptyList(),
                        detectedCountry = null
                    )
                }
            }
        }
    }

    private fun mapIsoToDisplayName(isoCode: String): String {
        return try {
            Locale("", isoCode).displayCountry
        } catch (e: Exception) {
            isoCode
        }
    }

    companion object {
        private const val TAG = "EmergencyNumbersVM"
    }

}
