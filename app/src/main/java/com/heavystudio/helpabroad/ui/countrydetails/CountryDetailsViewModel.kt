package com.heavystudio.helpabroad.ui.countrydetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.core.AppConfig
import com.heavystudio.helpabroad.data.local.dto.CountryDetails
import com.heavystudio.helpabroad.data.settings.SettingsRepository
import com.heavystudio.helpabroad.domain.repository.CountryRepository
import com.heavystudio.helpabroad.ui.model.UiCountryDetails
import com.heavystudio.helpabroad.ui.model.UiEmergencyService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for the Country Details screen.
 *
 * This ViewModel is responsible for fetching the details of a specific country, including its
 * name and a list of emergency services, based on a `countryId` passed through navigation arguments.
 * It observes user settings, such as "direct call" and "confirm before call," to manage the UI
 * state and user interaction flows appropriately.
 *
 * The final UI state, [CountryDetailsUiState], is exposed as a [StateFlow] which is a combination of:
 * 1. The fetched country details, mapped to a UI-friendly model.
 * 2. The current values of user-configurable settings from [SettingsRepository].
 * 3. The internal state for handling the call confirmation dialog.
 *
 * It determines the appropriate language for display names, falling back to English if the
 * device's language is not supported.
 *
 * @param countryRepository Repository for fetching country and emergency service data.
 * @param settingsRepository Repository for accessing user preferences.
 * @param savedStateHandle Handle to access navigation arguments, used here to retrieve the `countryId`.
 *
 * @author Heavy Studio.
 * @since 0.2.0 Creation of the VM.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CountryDetailsViewModel @Inject constructor(
    private val countryRepository: CountryRepository,
    private val settingsRepository: SettingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // --- State for call confirmation ---
    private val _confirmationState = MutableStateFlow<String?>(null)

    // --- Language determination ---
    private val effectiveLangCode: String = run {
        val deviceLang = Locale.getDefault().language
        if (deviceLang in AppConfig.supportedLanguages) deviceLang else "en"
    }

    // --- Flow for the country ID from navigation ---
    // We get the countryId argument passed in the route
    private val _countryIdFlow: Flow<Int> = savedStateHandle
        .getStateFlow("countryId", -1)
        .filter { it != -1 } // Only proceed once we have a valid ID

    // --- Flow for loading country details based on the ID
    private val _countryDetailsFlow: Flow<UiCountryDetails?> = _countryIdFlow
        .flatMapLatest { id ->
            countryRepository.getCountryDetails(id)
        }
        .map { details ->
            // Map the database DTO to our UI model
            if (details != null) mapToUiModel(details) else null
        }

    // --- The final UI state, combining details and settings ---
    val uiState: StateFlow<CountryDetailsUiState> = combine(
        _countryDetailsFlow,
        settingsRepository.directCallFlow,
        settingsRepository.confirmBeforeCallFlow,
        _confirmationState
    ) { details, isDirectCall, isConfirm, numberToConfirm ->
        CountryDetailsUiState(
            countryDetails = details,
            isLoading = details == null,
            isDirectCallEnabled = isDirectCall,
            isConfirmBeforeCallEnabled = isConfirm,
            numberToConfirmBeforeCall = numberToConfirm
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CountryDetailsUiState()
    )


    /**
     * Handles the click event on an emergency number.
     *
     * If both the "direct call" and "confirm before call" settings are enabled,
     * this function updates the state to show a confirmation dialog. Otherwise,
     * it does nothing, and the call is expected to be handled directly by the UI.
     *
     * @param number The phone number that was clicked.
     */
    fun onEmergencyNumberClicked(number: String) {
        // Only show confirmation if both settings are enabled
        if (uiState.value.isDirectCallEnabled && uiState.value.isConfirmBeforeCallEnabled) {
            _confirmationState.value = number
        }
    }

    /**
     * Dismisses the call confirmation dialog.
     *
     * This function is called when the user cancels the call confirmation dialog,
     * resetting the confirmation state to null and hiding the dialog from the UI.
     */
    fun onCallConfirmationDismissed() {
        _confirmationState.value = null
    }

    // --- Private Helper to map data ---
    private fun mapToUiModel(details: CountryDetails): UiCountryDetails {
        val countryIsoCode = details.country.isoCode
        val countryName = details.names.find { it.languageCode == effectiveLangCode }?.name
            ?: details.names.find { it.languageCode == "en" }?.name
            ?: countryIsoCode

        val services = details.services.mapNotNull { serviceDetails ->
            var serviceName = serviceDetails.names.find { it.languageCode == effectiveLangCode }?.name
            if (serviceName == null) serviceName = serviceDetails.names.find { it.languageCode == "en" }?.name
            if (serviceName != null) UiEmergencyService(
                code = serviceDetails.type.serviceCode,
                name = serviceName,
                number = serviceDetails.number.phoneNumber,
                numberType = serviceDetails.number.numberType
            ) else null
        }

        return UiCountryDetails(
            countryName = countryName,
            countryIsoCode = countryIsoCode,
            services = services
        )
    }
}