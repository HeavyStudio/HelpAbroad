package com.heavystudio.helpabroad.data.location

import android.location.Location

interface LocationManager {
    fun hasLocationPermission(): Boolean
    suspend fun getCurrentLocation(): LocationResultWrapper
    suspend fun getCountryCodeFromLocation(location: Location): String?
}

sealed interface LocationResultWrapper {
    object NoPermission : LocationResultWrapper
    object SettingsNotOptimal : LocationResultWrapper
    data class Success(val location: Location) : LocationResultWrapper
    data class Error(val exception: Exception) : LocationResultWrapper
}