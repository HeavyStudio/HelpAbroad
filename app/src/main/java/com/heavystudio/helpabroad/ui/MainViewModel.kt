package com.heavystudio.helpabroad.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.data.repository.UserPreferencesRepository
import com.heavystudio.helpabroad.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // This StateFlow will hold the start destination
    val startDestination: StateFlow<String?> =
        combine(
            userPreferencesRepository.isFirstLaunch,
            userPreferencesRepository.isPermissionsSetupCompleted
        ) { isFirstLaunch, isPermissionsSetupCompleted ->
            when {
                isFirstLaunch -> Routes.WELCOME
                !isPermissionsSetupCompleted -> Routes.PERMISSIONS
                else -> Routes.HOME
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}