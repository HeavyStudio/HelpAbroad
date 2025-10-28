package com.heavystudio.helpabroad.ui.settings

import com.heavystudio.helpabroad.data.settings.AppTheme

/**
 * Represents the UI state for the settings screen.
 *
 * @param theme The current theme setting of the application (e.g., Light, Dark, or System default).
 * @param isDirectCallEnabled A boolean flag indicating whether the direct call feature is enabled.
 * @param isConfirmBeforeCallEnabled A boolean flag indicating whether a confirmation dialog should
 *                                   be shown before making a call.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the data class.
 */
data class SettingsUiState(
    val theme: AppTheme = AppTheme.SYSTEM,
    val isDirectCallEnabled: Boolean = false,
    val isConfirmBeforeCallEnabled: Boolean = true
)
