package com.heavystudio.helpabroad.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.data.repository.OnboardingInterface
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
class OnboardingViewModel @Inject constructor(
    private val onboardingInterface: OnboardingInterface
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

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
        viewModelScope.launch {
            onboardingInterface.setOnboardingComplete(true)
        }
    }

    // For testing only, remove in prod
    fun resetOnboardingStatus() {
        viewModelScope.launch {
            onboardingInterface.setOnboardingComplete(false)
        }
    }
}