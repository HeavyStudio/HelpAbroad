package com.heavystudio.helpabroad.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val fused: FusedLocationProviderClient,
    private val context: Context
) : LocationRepository {

    @SuppressLint("MissingPermission")
    override suspend fun tryGetQuickLocation(timeoutMillis: Long): Location? {
        // 1. Try "current location" fast path with timeout
        val fresh = withTimeoutOrNull(timeoutMillis) {
            try {
                val req = CurrentLocationRequest.Builder()
                    .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                    .setMaxUpdateAgeMillis(0)
                    .build()
                fused.getCurrentLocation(req, CancellationTokenSource().token).await()
            } catch (_: Exception) { null }
        }
        if (fresh != null) return fresh

        // 2. Fallback to last known
        return try {
            fused.lastLocation.await()
        } catch (_: Exception) {
            null
        }
    }
}