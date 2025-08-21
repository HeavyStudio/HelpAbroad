package com.heavystudio.helpabroad.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import okio.IOException
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationRepositoryImpl @Inject constructor(
    private val fused: FusedLocationProviderClient,
    private val context: Context
) : LocationRepository {

    @SuppressLint("MissingPermission")
    override suspend fun tryGetQuickLocation(timeoutMillis: Long): Location? {
        if (!hasLocationPermission()) {
            throw SecurityException("Location permission not granted. " +
                    "Please grant permission to access location.")
        }

        val freshLocation: Location? = withTimeoutOrNull(timeoutMillis) {
            try {
                val req = CurrentLocationRequest.Builder()
                    .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                    .setMaxUpdateAgeMillis(0)
                    .build()
                fused.getCurrentLocation(req, CancellationTokenSource().token).await()
            } catch (e: Exception) { null }
        }

        if (freshLocation != null) return freshLocation

        return try {
            fused.lastLocation.await()
        } catch (e: Exception) { null }
    }

    override fun getCurrentLocationData(): Flow<LocationData> = flow {
        Log.d("HomeVM_LocationRepoImpl", "getCurrentLocationData flow builder started.")
        if (!hasLocationPermission()) {
            Log.e("HomeVM_LocationRepoImpl", "Location permission not granted.")
            throw SecurityException("Location permission not granted. " +
                    "Please grant permission to access location.")
        }

        val location: Location? = try {
            Log.d("HomeVM_LocationRepoImpl", "Attempting to get quick location...")
            tryGetQuickLocation(QUICK_LOCATION_DEFAULT_TIMEOUT)
        } catch (e: Exception) {
            Log.e("HomeVM_LocationRepoImpl", "Error getting quick location: ${e.message}", e)
            throw IOException("Unable to determine current location. " +
                    "Ensure location services are enabled.")
        }

        if (location == null) {
            Log.e("HomeVM_LocationRepoImpl", "tryGetQuickLocation returned null.")
            throw IOException("Unable to determine current location." +
                    "Ensure location services are enabled.")
        }
        Log.d("HomeVM_LocationRepoImpl", "Quick location obtained: $location")

        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>?
        var countryNameFromGeo: String? = null
        var countryCodeFromGeo: String? = null
        val fullAddressString: String

        try {
            Log.d("HomeVM_LocationRepoImpl", "Attempting geocoding...")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                addresses = suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        GEOCODER_MAX_RESULTS
                    ) { geocodedAddresses ->
                        Log.d("HomeVM_LocationRepoImpl", "GeocodeListener (API 33+) " +
                                "received: $geocodedAddresses")
                        if (continuation.isActive) {
                            if (geocodedAddresses.isNotEmpty()) {
                                continuation.resume(geocodedAddresses)
                            } else {
                                continuation.resume(emptyList())
                            }
                        }
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    GEOCODER_MAX_RESULTS
                )
                Log.d("HomeVM_LocationRepoImpl", "geocoder.getFromLocation (pre-API 33) " +
                        "received: $addresses")
            }

            if (!addresses.isNullOrEmpty()) {
                val firstAddress = addresses[0]
                fullAddressString = formatAddress(firstAddress)
                countryNameFromGeo = firstAddress.countryName
                countryCodeFromGeo = firstAddress.countryCode
                Log.d("HomeVM_LocationRepoImpl", "Geocoding successful: " +
                        "Address='$fullAddressString', " +
                        "Country='$countryNameFromGeo', " +
                        "Code='$countryCodeFromGeo'")
            } else {
                Log.w("HomeVM_LocationRepoImpl", "Geocoder returned no addresses.")
                fullAddressString = "No address found for the current location."
            }
        } catch (e: IOException) {
            Log.e("LocationRepoImpl", "IOException during geocoding: ${e.message}", e)
            throw IOException("Geocoder service error: ${e.message}", e)
        } catch (e: IllegalArgumentException) {
            Log.e("LocationRepoImpl", "IllegalArgumentException during geocoding: ${e.message}", e)
            throw IllegalArgumentException("Invalid location coordinates provided for " +
                    "geocoding.", e)
        } catch (e: Exception) {
            Log.e("LocationRepoImpl", "Unexpected exception during geocoding: ${e.message}", e) // Add this catch-all
            throw IOException("Unexpected error during address lookup: ${e.message}", e)
        }

        val locationData = LocationData(
            fullAddress = fullAddressString,
            countryName = countryNameFromGeo,
            countryCode = countryCodeFromGeo
        )

        Log.d("HomeVM_LocationRepoImpl", "EMITTING LocationData: $locationData")
        emit(locationData)
        Log.d("HomeVM_LocationRepoImpl", "Finished EMITTING LocationData")
    }.flowOn(Dispatchers.IO)

    private fun hasLocationPermission(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocationGranted || coarseLocationGranted
    }

    private fun formatAddress(address: Address): String {
        val addressParts = mutableListOf<String?>().apply {
            add(address.getAddressLine(0))
        }
        return addressParts.filterNotNull().joinToString(separator = ", ").ifEmpty {
            "Address details not available"
        }
    }

    companion object {
        private const val QUICK_LOCATION_DEFAULT_TIMEOUT = 10000L
        private const val GEOCODER_MAX_RESULTS = 1
    }
}