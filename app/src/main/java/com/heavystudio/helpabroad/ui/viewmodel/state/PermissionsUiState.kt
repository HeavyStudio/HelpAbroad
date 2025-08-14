package com.heavystudio.helpabroad.ui.viewmodel.state

import androidx.annotation.StringRes

// Enum to represent the status of an individual permission
enum class PermissionStatus {
    UNKNOWN, // Initial state
    REQUESTED, // Permission requested, waiting for an answer
    GRANTED,
    DENIED_ONCE, // User denied the permission once
    DENIED_PERMANENTLY // User denied the permission permanently
}

// Data class for the information of a specific permission to display
data class PermissionInfo(
    val permission: String,
    @StringRes val titleResId: Int,
    @StringRes val descriptionResId: Int,
    @StringRes val rationaleResId: Int? = null,
    val isCrucial: Boolean
)

// Data class for the state of a permission in the UI
data class PermissionDisplayState(
    val info: PermissionInfo,
    val status: PermissionStatus = PermissionStatus.UNKNOWN
)

// The overall state for the permissions screen
data class PermissionsScreenUiState(
    val permissionsToRequest: List<PermissionDisplayState> = emptyList(),
    val allPermissionsGranted: Boolean = false,
    val canContinue: Boolean = false
)