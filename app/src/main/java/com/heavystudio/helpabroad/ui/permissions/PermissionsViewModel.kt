package com.heavystudio.helpabroad.ui.permissions

import android.Manifest
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.test.internal.platform.content.PermissionGranter
import com.heavystudio.helpabroad.R
import com.heavystudio.helpabroad.data.location.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PermissionsNavEvent {
    data object GoHome: PermissionsNavEvent()
    data object GoCountrySelection : PermissionsNavEvent()
}

@HiltViewModel
open class PermissionsViewModel @Inject constructor(
    private val locationRepo: LocationRepository
) : ViewModel() {

    // Order: Location, Call, SMS
    private val _items = MutableStateFlow(
        listOf(
            PermissionItemUiState(
                titleRes = R.string.title_location,
                descriptionRes = R.string.desc_location,
                rationaleRes = R.string.rationale_location
            ),
            PermissionItemUiState(
                titleRes = R.string.title_call_phone,
                descriptionRes = R.string.desc_call_phone,
                rationaleRes = R.string.rationale_send_sms
            ),
            PermissionItemUiState(
                titleRes = R.string.title_send_sms,
                descriptionRes = R.string.desc_send_sms,
                rationaleRes = R.string.rationale_send_sms
            )
        )
    )

    val items: StateFlow<List<PermissionItemUiState>> = _items.asStateFlow()

    // Track per-permission status by manifest string for clarity
    private val _statusByPermission = MutableStateFlow(
        mapOf(
            Manifest.permission.ACCESS_FINE_LOCATION to PermissionStatus.UNKNOWN,
            Manifest.permission.CALL_PHONE to PermissionStatus.UNKNOWN,
            Manifest.permission.SEND_SMS to PermissionStatus.UNKNOWN
        )
    )

    val canContinue: StateFlow<Boolean> = _statusByPermission
        .map { map ->  map.values.all { it != PermissionStatus.UNKNOWN } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val navEventsChannel = Channel<PermissionsNavEvent>(Channel.BUFFERED)
    val navEvents: Flow<PermissionsNavEvent> = navEventsChannel.receiveAsFlow()

    fun onPermissionStatusChanged(index: Int, status: PermissionStatus) {
        _items.update { current ->
            current.toMutableList().also { list ->
                list[index] = list[index].copy(status = status)
            }
        }

        val permissionKey = when (index) {
            0 -> Manifest.permission.ACCESS_FINE_LOCATION
            1 -> Manifest.permission.CALL_PHONE
            else -> Manifest.permission.SEND_SMS
        }
        _statusByPermission.update { it + (permissionKey to status) }
    }

    fun onContinue() {
        val locationStatus = _statusByPermission.value[Manifest.permission.ACCESS_FINE_LOCATION]
        if (PermissionStatus.GRANTED != locationStatus) {
            viewModelScope.launch {
                val loc: Location? = try {
                    locationRepo.tryGetQuickLocation(timeoutMillis = 2000L)
                } catch (_: Exception) {
                    null
                }

                if (loc != null) navEventsChannel.send(PermissionsNavEvent.GoHome)
                else navEventsChannel.send(PermissionsNavEvent.GoCountrySelection)
            }
        } else {
            viewModelScope.launch {
                navEventsChannel.send(PermissionsNavEvent.GoCountrySelection)
            }
        }
    }
}