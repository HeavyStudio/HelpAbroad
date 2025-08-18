package com.heavystudio.helpabroad.ui.permissions

enum class PermissionStatus {
    UNKNOWN,
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED
}

data class PermissionItemUiState(
    val titleRes: Int,
    val descriptionRes: Int,
    val rationaleRes: Int,
    val status: PermissionStatus = PermissionStatus.UNKNOWN
)