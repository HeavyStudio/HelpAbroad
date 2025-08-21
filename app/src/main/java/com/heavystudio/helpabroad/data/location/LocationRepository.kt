package com.heavystudio.helpabroad.data.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

data class LocationData(
    val fullAddress: String,
    val countryName: String?,
    val countryCode: String?
)

interface LocationRepository {
    /**
     * Tries to get a quick location fix.
     *
     * This method attempts to retrieve the device's current location quickly,
     * typically using the last known location or a less precise method if a
     * highly accurate fix would take too long.
     *
     * @param timeoutMillis The maximum time in milliseconds to wait for a location fix.
     * @return The [Location] if a fix is obtained within the timeout, otherwise `null`.
     */
    suspend fun tryGetQuickLocation(timeoutMillis: Long): Location?

    /**
     * Retrieves the current location address as a Flow of strings.
     *
     * This function observes location updates and, for each new location,
     * attempts to reverse geocode it to an address string.
     *
     * @return A Flow emitting address strings as they become available.
     *         Emits an empty string if geocoding fails or no address is found.
     */
    fun getCurrentLocationData(): Flow<LocationData>
}