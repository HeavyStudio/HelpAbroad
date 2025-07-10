package com.heavystudio.helpabroad.data.repository

import androidx.annotation.RequiresPermission
import com.heavystudio.helpabroad.data.model.EmergencyContact
import kotlinx.coroutines.flow.Flow
import java.util.jar.Manifest

interface EmergencyRepository {
    @RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun getEmergencyContact(): Flow<List<EmergencyContact>>
}