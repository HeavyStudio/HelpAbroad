package com.heavystudio.helpabroad.ui.settings

import com.heavystudio.helpabroad.data.settings.AppTheme

data class SettingsUiState(
    val theme: AppTheme = AppTheme.SYSTEM,
    val isDirectCallEnabled: Boolean = false,
    val isConfirmBeforeCallEnabled: Boolean = true
)
