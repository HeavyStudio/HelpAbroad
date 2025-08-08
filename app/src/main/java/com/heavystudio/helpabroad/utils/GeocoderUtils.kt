package com.heavystudio.helpabroad.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okio.IOException
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun getFullAddressLine(
    context: Context,
    latitude: Double,
    longitude: Double
): String? {
    if (!Geocoder.isPresent()) {
        Log.e("GeocoderUtils", "Geocoder not available on this device.")
        return null
    }

    val geocoder = Geocoder(context, Locale.getDefault())

    return try {
        val addresses = getAddressesHelper(geocoder, latitude, longitude)
        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            Log.d("GeocoderUtils", "Full Address Line 0: ${address.getAddressLine(0)}")
            Log.d("GeocoderUtils", "Street (Thoroughfare): ${address.thoroughfare}")
            Log.d("GeocoderUtils", "Locality (City): ${address.locality}")
            Log.d("GeocoderUtils", "Postal Code: ${address.postalCode}")
            address.getAddressLine(0)
        } else {
            Log.w("GeocoderUtils", "No address found for the given coordinates.")
            null
        }
    } catch (e: IOException) {
        // Handles errors from getAddressesHelper if they are IOExceptions
        // or errors during Geocoder instantiation if it were to throw IOException (unlikely here)
        Log.e("GeocoderUtils", "Geocoding failed due to I/O or network issue.", e)
        null
    } catch (e: IllegalArgumentException) {
        // Handles invalid latitude/longitude arguments if they were somehow passed
        Log.e("GeocoderUtils", "Invalid latitude/longitude provided.", e)
        null
    } catch (e: Exception) {
        // Catch any other unexpected errors from the geocoding process
        Log.e("GeocoderUtils", "An unexpected error occurred during geocoding.", e)
        null
    }
}

private suspend fun getAddressesHelper(
    geocoder: Geocoder,
    latitude: Double,
    longitude: Double,
    maxResults: Int = 1
): List<Address>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // For Android 13+, use the new GeocodeListener
        suspendCancellableCoroutine { continuation ->
            val listener = object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    if (continuation.isActive) {
                        continuation.resume(addresses)
                    }
                }

                override fun onError(errorMessage: String?) {
                    if (continuation.isActive) {
                        Log.e(
                            "GeocoderUtils",
                            "Geocoding error (API 33+): ${errorMessage ?: "Unknown error"}"
                        )
                        continuation.resumeWithException(
                            IOException("Geocoding failed (API 33+): ${errorMessage ?: "Unknown error"}")
                        )
                    }
                }
            }
            geocoder.getFromLocation(latitude, longitude, maxResults, listener)
        }
    } else {
        // Older versions
        withContext(Dispatchers.IO) {
            try {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latitude, longitude, maxResults)
            } catch (e: IOException) {
                Log.e("GeocoderUtils", "IOException in deprecated getFromLocation", e)
                throw e
            } catch (e: IllegalArgumentException) {
                Log.e("GeocoderUtils", "IllegalArgumentException in deprecated getFromLocation", e)
                throw e
            } catch (e: Exception) {
                Log.e("GeocoderUtils", "Exception in deprecated getFromLocation", e)
                throw e
            }
        }
    }
}