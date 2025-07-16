package com.heavystudio.helpabroad.data.repository

import androidx.annotation.RequiresPermission
import com.heavystudio.helpabroad.data.model.EmergencyContact
import kotlinx.coroutines.flow.Flow
import android.Manifest

interface EmergencyRepository {
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getEmergencyContact(): Flow<List<EmergencyContact>>

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getCurrentNetworkCountryIso(): String?
}