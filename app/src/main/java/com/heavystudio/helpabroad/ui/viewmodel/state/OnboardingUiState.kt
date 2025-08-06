package com.heavystudio.helpabroad.ui.viewmodel.state

sealed class PermissionStatus {
    object NotRequested : PermissionStatus()
    object Requested : PermissionStatus()
    object Granted : PermissionStatus()
    data class Denied(val permanently: Boolean) : PermissionStatus()
}

data class OnboardingUiState(
    val locationPermissionStatus: PermissionStatus = PermissionStatus.NotRequested,
    val callPermissionStatus: PermissionStatus = PermissionStatus.NotRequested,
    val smsPermissionStatus: PermissionStatus = PermissionStatus.NotRequested
)
