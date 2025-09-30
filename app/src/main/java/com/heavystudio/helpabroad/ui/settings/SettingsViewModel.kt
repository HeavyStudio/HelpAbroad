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

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // Expose un seul StateFlow pour l'état de l'UI,
    // on le construit en combinant les 3 flows de SettingsRepository
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

    // ----- Fonction pour que l'UI puisse notifier des changements -----
    fun onThemeChanged(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    fun onDirectCallToggled(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDirectCall(isEnabled)
        }
    }

    fun onConfirmBeforeCallToggled(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setConfirmBeforeCall(isEnabled)
        }
    }
}