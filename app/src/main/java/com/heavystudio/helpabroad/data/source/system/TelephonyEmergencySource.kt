package com.heavystudio.helpabroad.data.source.system

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.telephony.emergency.EmergencyNumber
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelephonyEmergencySource @Inject constructor(
    private val telephonyManager: TelephonyManager,
    @param:ApplicationContext private val context: Context
) {

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getEmergencyNumbers(): List<String> {
        if (ContextCompat.checkSelfPermission(context, READ_PHONE_STATE) != PERMISSION_GRANTED) {
            Log.w(TAG, "READ_PHONE_STATE permission not granted for getEmergencyNumberList.")
            // Fallback to common emergency numbers if permission is denied
            return listOf("112", "911")
        }

        try {
            // The map can contain different categories of emergency numbers
            val emergencyNumbersMap = telephonyManager.emergencyNumberList

            // Flatten the lists of EmergencyNumber objects from all categories
            val allEmergencyNumbersAsObjects: List<EmergencyNumber> = emergencyNumbersMap.values.flatten()

            // Map each EmergencyNumber object to its String representation (the number itself)
            val emergencyNumberStrings: List<String> = allEmergencyNumbersAsObjects.map { emergencyNumber ->
                emergencyNumber.number
            }

            // Get distinct numbers and then handle the ifEmpty case
            return emergencyNumberStrings.distinct().ifEmpty {
                Log.w(TAG, "TelephonyManager.emergencyNumberList returned empty or null." +
                        "Falling back.")
                getFallbackEmergencyNumbers()
            }
        } catch (se: SecurityException) {
            Log.e(TAG, "SecurityException in getEmergencyNumbers", se)
            return getFallbackEmergencyNumbers()
        } catch (e: Exception) {
            Log.e(TAG, "Unknown Exception in getEmergencyNumbers", e)
            return getFallbackEmergencyNumbers()
        }
    }

    private fun getFallbackEmergencyNumbers(): List<String> {
        val numbers = mutableListOf<String>()
        numbers.add("112") // EU

        // Try to get country-specific numbers if possible
        try {
            val countryIso = telephonyManager.networkCountryIso?.uppercase()
            Log.d(TAG, "Network Country ISO: $countryIso")
            when (countryIso) {
                "US", "CA" -> numbers.add("911") // US, Canada, US territories
                "GB" -> numbers.add("999") // UK
                "AU" -> numbers.add("000") // Australia
            }
        } catch (se: SecurityException) {
            Log.e(TAG, "SecurityException getting networkCountryIso", se)
        } catch (e: Exception) {
            Log.e(TAG, "Unknown Exception getting networkCountryIso", e)
        }
        return numbers.distinct()
    }

    companion object {
        private const val TAG = "SystemEmergencySource"
        private const val READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE
        private const val PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED
    }
}