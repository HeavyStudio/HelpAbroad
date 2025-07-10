package com.heavystudio.helpabroad.data.repository

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import com.heavystudio.helpabroad.data.model.EmergencyContact
import com.heavystudio.helpabroad.data.source.system.TelephonyEmergencySource
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

@Singleton
class EmergencyRepositoryImpl @Inject constructor(
    private val systemSource: TelephonyEmergencySource
) : EmergencyRepository {

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    override fun getEmergencyContact(): Flow<List<EmergencyContact>> = flow {
        Log.d(TAG, "Fetching detailed emergency contacts from system source.")
        try {
            val contacts = systemSource.getDetailedEmergencyContacts()
            emit(contacts)
        } catch (se: SecurityException) {
            Log.e(TAG, "SecurityException fetching emergency contacts: ${se.message}", se)
            emit(emptyList())
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching detailed emergency contacts: ${e.message}", e)
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
        .catch { throwable ->
            Log.e(TAG, "Unhandled error in getEmergencyContacts flow: ${throwable.message}", throwable)
        }

    companion object {
        private const val TAG = "EmergencyRepositoryImpl"
    }
}