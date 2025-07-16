package com.heavystudio.helpabroad.ui.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.R
import com.heavystudio.helpabroad.data.model.EmergencyContact
import com.heavystudio.helpabroad.data.model.ErrorKeys
import com.heavystudio.helpabroad.data.repository.EmergencyRepository
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
    val errorKey: String? = null,
    val errorDetails: String? = null,
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
                    permissionRequiredMessage = ErrorKeys.NO_PERMISSION_ERROR,
                    emergencyContacts = emptyList(),
                    errorKey = null
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
                                errorKey = null,
                                errorDetails = null,
                                permissionRequiredMessage = null,
                                detectedCountry = null
                            )
                        }
                    }
                    .catch { exception ->
                        Log.e(TAG, "Error collecting emergency contacts from repository", exception)
                        val exceptionMessage = exception.localizedMessage ?: ""
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorKey = ErrorKeys.LOAD_FAILED_ERROR,
                                errorDetails = exceptionMessage,
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

                        val finalErrorKey = if (
                            contacts.isEmpty() &&
                            _uiState.value.errorKey == null &&
                            _uiState.value.permissionRequiredMessage == null
                        ) {
                            ErrorKeys.NO_CONTACTS_FOUND_FOR_COUNTRY_ERROR
                        } else {
                            _uiState.value.errorKey
                        }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                emergencyContacts = contacts,
                                errorKey = finalErrorKey,
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
                        permissionRequiredMessage = ErrorKeys.PERMISSION_DENIED_ERROR,
                        errorKey = null,
                        emergencyContacts = emptyList(),
                        detectedCountry = null
                    )
                }
            }
        }
    }

    private fun mapIsoToDisplayName(isoCode: String): String {
        return try {
            val userLocale = Locale.getDefault()
            val countryOnlyLocale = Locale.Builder().setRegion(isoCode.uppercase(Locale.ROOT)).build()
            countryOnlyLocale.getDisplayCountry(userLocale)
        } catch (e: Exception) {
            Log.e(TAG, "Error mapping ISO code '$isoCode' to display name: ${e.message}")
            isoCode
        }
    }

    companion object {
        private const val TAG = "EmergencyNumbersVM"
    }

}
