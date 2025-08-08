package com.heavystudio.helpabroad.ui.viewmodel.state

sealed class PermissionStatus {
    object NotRequested : PermissionStatus()
    object Requested : PermissionStatus()
    object Granted : PermissionStatus()
    object FineGranted : PermissionStatus()
    object CoarseGranted : PermissionStatus()
    object Denied : PermissionStatus()
}

data class PermissionsUiState(
    val locationPermissionStatus: PermissionStatus = PermissionStatus.NotRequested,
    val callPermissionStatus: PermissionStatus = PermissionStatus.NotRequested,
    val smsPermissionStatus: PermissionStatus = PermissionStatus.NotRequested
)
