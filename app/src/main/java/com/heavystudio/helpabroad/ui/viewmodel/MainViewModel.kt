package com.heavystudio.helpabroad.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.data.repository.UserPreferencesRepository
import com.heavystudio.helpabroad.ui.navigation.StartDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // This StateFlow will hold the start destination
    val startDestination: StateFlow<StartDestination?> =
        userPreferencesRepository.isFirstLaunch.map { isFirstLaunch ->
            if (isFirstLaunch) {
                StartDestination.Welcome
            } else {
                StartDestination.Permissions
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}