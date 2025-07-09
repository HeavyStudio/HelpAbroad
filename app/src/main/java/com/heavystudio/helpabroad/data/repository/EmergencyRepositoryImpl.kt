package com.heavystudio.helpabroad.data.repository

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import com.heavystudio.helpabroad.data.source.system.TelephonyEmergencySource
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

@Singleton
class EmergencyRepositoryImpl @Inject constructor(
    private val systemSource: TelephonyEmergencySource
) : EmergencyRepository {

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    override fun getSystemEmergencyNumbers(): Flow<List<String>> = flow {
        Log.d(TAG, "Fetching system emergency numbers.")

        try {
            val numbers = systemSource.getEmergencyNumbers()
            emit(numbers)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching system emergency numbers: ${e.message}", e)
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    companion object {
        private const val TAG = "EmergencyRepositoryImpl"
    }
}