package com.heavystudio.helpabroad.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume

class AndroidLocationProvider(
    private val context: Context
) : LocationManager {

    private val tag = "AndroidLocationProvider"

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val settingsClient: SettingsClient by lazy {
        LocationServices.getSettingsClient(context)
    }

    override fun hasLocationPermission(): Boolean {
        val coarsePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val finePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return coarsePermission || finePermission
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LocationResultWrapper {
        if (!hasLocationPermission()) {
            return LocationResultWrapper.NoPermission
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,5000L
        )
            .setWaitForAccurateLocation(true)
            .setMaxUpdateDelayMillis(10000L)
            .setMaxUpdates(1)
            .build()

        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()

        try {
            settingsClient.checkLocationSettings(locationSettingsRequest).await()
        } catch (e: ResolvableApiException) {
            return LocationResultWrapper.SettingsNotOptimal
        } catch (e: Exception) {
            return LocationResultWrapper.Error(e)
        }

        return suspendCancellableCoroutine { continuation ->
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    fusedLocationClient.removeLocationUpdates(this)
                    locationResult.lastLocation?.let {
                        if (continuation.isActive) {
                            continuation.resume(LocationResultWrapper.Success(it))
                        }
                    } ?: run {
                        if (continuation.isActive) {
                            continuation.resume(
                                LocationResultWrapper.Error(
                                    Exception("Failed to get location from callback.")
                                )
                            )
                        }
                    }
                }

                override fun onLocationAvailability(availability: LocationAvailability) {
                    if (!availability.isLocationAvailable && continuation.isActive) {
                        fusedLocationClient.removeLocationUpdates(this)
                        continuation.resume(LocationResultWrapper.Error(
                            Exception("Location became unavailable.")
                        ))
                    }
                }
            }

            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (securityException: SecurityException) {
                if (continuation.isActive) {
                    continuation.resume(LocationResultWrapper.NoPermission)
                }
            } catch (exception: Exception) {
                if (continuation.isActive) {
                    continuation.resume(LocationResultWrapper.Error(exception))
                }
            }

            continuation.invokeOnCancellation {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    override suspend fun getCountryCodeFromLocation(location: Location): String? {
        if (!Geocoder.isPresent()) {
            return null
        }

        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            withContext(Dispatchers.IO) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { continuation ->
                        geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1,
                            object : Geocoder.GeocodeListener {
                                override fun onGeocode(addresses: MutableList<Address>) {
                                    if (continuation.isActive) {
                                        continuation.resume(addresses.firstOrNull()?.countryCode)
                                    }
                                }

                                override fun onError(errorMessage: String?) {
                                    super.onError(errorMessage)
                                    if (continuation.isActive) {
                                        Log.e(tag, "Geocoder (API33+) error: $errorMessage")
                                        continuation.resume(null)
                                    }
                                }
                            }
                        )
                        continuation.invokeOnCancellation {  }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    )
                    addresses?.firstOrNull()?.countryCode
                }
            }
        } catch (ioException: IOException) {
            Log.e(tag, "IO Exception: $ioException", ioException)
            null
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(tag,
                "Illegal Argument Exception: $illegalArgumentException",
                illegalArgumentException)
            null
        }
    }
}