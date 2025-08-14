package com.heavystudio.helpabroad.ui.viewmodel

import android.Manifest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.R
import com.heavystudio.helpabroad.data.repository.UserPreferencesRepository
import com.heavystudio.helpabroad.ui.viewmodel.state.PermissionInfo
import com.heavystudio.helpabroad.utils.permissionchecker.SystemPermissionChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val systemPermissionChecker: SystemPermissionChecker
) : ViewModel() {

    // Expose isPermissionsSetupCompleted as a StateFlow
    val isPermissionsSetupCompleted: StateFlow<Boolean> =
        userPreferencesRepository.isPermissionsSetupCompleted.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private val appPermissionsConfig = listOf(
        PermissionInfo(
            permission = Manifest.permission.ACCESS_FINE_LOCATION,
            titleResId = R.string.title_location, // TODO
            descriptionResId = R.string.desc_location, // TODO
            rationaleResId = R.string.rationale_location, // TODO
            isCrucial = true
        ),

        PermissionInfo(
            permission = Manifest.permission.CALL_PHONE,
            titleResId = R.string.title_call_phone, // TODO
            descriptionResId = R.string.desc_call_phone, // TODO
            rationaleResId = R.string.rationale_call_phone, // TODO
            isCrucial = false
        ),

        PermissionInfo(
            permission = Manifest.permission.SEND_SMS,
            titleResId = R.string.title_send_sms, // TODO
            descriptionResId = R.string.desc_send_sms, // TODO
            rationaleResId = R.string.rationale_send_sms, // TODO
            isCrucial = false
        )
    )

    fun onContinueClicked() {
        viewModelScope.launch {
            userPreferencesRepository.updateIsPermissionsSetupCompleted(true)
        }
    }
}