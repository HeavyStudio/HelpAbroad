package com.heavystudio.helpabroad.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.data.settings.AppTheme
import com.heavystudio.helpabroad.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the settings screen.
 *
 * This ViewModel is responsible for exposing the current settings state to the UI
 * and handling user interactions to update those settings. It interacts with the
 * [SettingsRepository] to persist and retrieve user preferences.
 *
 * @param settingsRepository The repository for accessing and modifying application settings.
 *
 * @author Heavy Studio.
 * @since 0.2.0 Divided MainViewModel into specific ViewModels for each screen.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.themeFlow,
        settingsRepository.directCallFlow,
        settingsRepository.confirmBeforeCallFlow
    ) { theme, isDirectCall, isConfirmBefore ->
        SettingsUiState(
            theme = theme,
            isDirectCallEnabled = isDirectCall,
            isConfirmBeforeCallEnabled = isConfirmBefore
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    /**
     * Handles the event when the user selects a new application theme.
     *
     * This function is called from the UI when a theme change is triggered. It launches a coroutine
     * within the [viewModelScope] to asynchronously update the theme preference in the
     * [SettingsRepository].
     *
     * @param theme The new [AppTheme] selected by the user.
     */
    fun onThemeChanged(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    /**
     * Handles the user action of toggling the "direct call" setting.
     *
     * This function is called from the UI when the user changes the state of the direct call
     * preference. It launches a coroutine in the [viewModelScope] to update the setting
     * in the [SettingsRepository] without blocking the main thread.
     *
     * @param isEnabled The new state for the direct call setting. `true` if direct calls should
     *                  be enabled, `false` otherwise.
     */
    fun onDirectCallToggled(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDirectCall(isEnabled)
        }
    }

    /**
     * Updates the user's preference for showing a confirmation dialog before making a call.
     *
     * This function is called when the user toggles the "confirm before call" switch in the settings UI.
     * It launches a coroutine to update the setting in the [SettingsRepository].
     *
     * @param isEnabled A boolean indicating the new state of the setting. `true` to enable the
     * confirmation dialog, `false` to disable it.
     */
    fun onConfirmBeforeCallToggled(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setConfirmBeforeCall(isEnabled)
        }
    }
}