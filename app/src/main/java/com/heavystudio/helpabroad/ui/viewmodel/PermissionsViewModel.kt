package com.heavystudio.helpabroad.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.data.repository.OnboardingInterface
import com.heavystudio.helpabroad.ui.viewmodel.state.PermissionsUiState
import com.heavystudio.helpabroad.ui.viewmodel.state.PermissionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionsViewModel @Inject constructor(
    private val onboardingInterface: OnboardingInterface
) : ViewModel() {

    private val _uiState = MutableStateFlow(PermissionsUiState())
    val uiState: StateFlow<PermissionsUiState> = _uiState.asStateFlow()

    val isOnboardingComplete: StateFlow<Boolean> =
        onboardingInterface.isOnboardingComplete()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )

    fun updateLocationPermissionStatus(status: PermissionStatus) {
        _uiState.update { it.copy(locationPermissionStatus = status) }
    }

    fun updateCallPermissionStatus(status: PermissionStatus) {
        _uiState.update { it.copy(callPermissionStatus = status) }
    }

    fun updateSmsPermissionStatus(status: PermissionStatus) {
        _uiState.update { it.copy(smsPermissionStatus = status) }
    }

    fun onOnboardingAttemptProceed() {
        Log.d("PERMISSIONS_VM", "onOnboardingAttemptProceed() CALLED") // Step 1 Log
        viewModelScope.launch {
            Log.d("PERMISSIONS_VM", "Coroutine for setOnboardingComplete starting...") // Step 1 Log
            onboardingInterface.setOnboardingComplete(true)
            Log.d("PERMISSIONS_VM", "onboardingInterface.setOnboardingComplete(true) FINISHED") // Step 1 Log
        }
    }

    // For testing only, remove in prod
    fun resetOnboardingStatus() {
        viewModelScope.launch {
            onboardingInterface.setOnboardingComplete(false)
        }
    }
}