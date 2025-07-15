package com.heavystudio.helpabroad.data.source.system

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.telephony.emergency.EmergencyNumber
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.heavystudio.helpabroad.data.model.EmergencyContact
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelephonyEmergencySource @Inject constructor(
    private val telephonyManager: TelephonyManager,
    @param:ApplicationContext private val context: Context
) {
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getDetailedEmergencyContacts(): List<EmergencyContact> {
        if (telephonyManager == null) {
            Log.w(TAG, "TelephonyManager is null. Returning fallback emergency contacts.")
            return getFallbackEmergencyContacts()
        }

        if (ContextCompat.checkSelfPermission(context, READ_PHONE_STATE) != PERMISSION_GRANTED) {
            Log.w(TAG, "READ_PHONE_STATE permission not granted for getDetailedEmergencyContacts." +
                    "Returning fallback emergency contacts.")
            return getFallbackEmergencyContacts()
        }

        val emergencyContactsResult = mutableListOf<EmergencyContact>()

        try {
            // Log the raw map for debugging.
            Log.d(TAG, "Raw emergencyNumbersMap from TelephonyManager: ${telephonyManager.emergencyNumberList}")

            val emergencyNumbersMap: Map<Int, List<EmergencyNumber>> = telephonyManager.emergencyNumberList
            if (emergencyNumbersMap.isEmpty()) {
                Log.w(TAG, "TelephonyManager.emergencyNumberList returned empty or null." +
                        "Attempting fallback.")
                return getFallbackEmergencyContacts(useNetworkCountryIso = true)
            }

            // Iterate through the map (outer grouping)
            emergencyNumbersMap.forEach { (mapGroupCategory, numberObjectList) ->
                // Iterate through each EmergencyNumber object in the list
                numberObjectList.forEach { emergencyNumberObject ->
                    val specificCategoriesList = emergencyNumberObject.emergencyServiceCategories
                    var type = "Urgences (non spécifié)"

                    if (specificCategoriesList.isNotEmpty()) {
                        var foundPreferredType = false
                        for (cat in specificCategoriesList) {
                            val mappedType = mapCategoryToTypeString(cat)
                            if (cat != EmergencyNumber.EMERGENCY_SERVICE_CATEGORY_UNSPECIFIED &&
                                !mappedType.startsWith("Urgences(catégorie:")) {
                                type = mappedType
                                foundPreferredType = true
                                break
                            }
                        }
                        if (!foundPreferredType) {
                            type = mapCategoryToTypeString(specificCategoriesList[0])
                        }
                    } else {
                        Log.w(TAG, "Number ${emergencyNumberObject.number} has empty " +
                                "specificCategoriesList. Map group was $mapGroupCategory")
                    }

                    // --- SPECIAL HANDLING FOR INTERNATIONAL NUMBERS ---
                    if (type == "Urgences (non spécifié)" || type.startsWith("Urgences (catégorie:")) {
                        when (emergencyNumberObject.number) {
                            "112" -> type = "Urgences (Europe)"
                            "911" -> type = "Urgences (Amérique du Nord)"
                            "999" -> type = "Urgences (Royaume-Uni)"
                            "000" -> type = "Urgences (Australie)"
                        }
                    }
                    // --- END OF SPECIAL HANDLING ---

                    // Log details for each number object
                    Log.d(TAG, "Processing Number: ${emergencyNumberObject.number}, \n" +
                            "Map Group Category: $mapGroupCategory, \n" +
                            "Specific Categories List: $specificCategoriesList, \n" +
                            "Mapped Type: $type, \n" +
                            "URNs: ${emergencyNumberObject.emergencyUrns}, \n" +
                            "Sources: ${emergencyNumberObject.emergencyNumberSources}")

                    emergencyContactsResult.add(
                        EmergencyContact(
                            number = emergencyNumberObject.number,
                            type = type
                        )
                    )
                }
            }

            return if (emergencyContactsResult.isNotEmpty()) {
                emergencyContactsResult.distinctBy { it.number }
            } else {
                Log.w(TAG, "Processed emergencyNumberList but result is empty. Using fallback.")
                getFallbackEmergencyContacts(useNetworkCountryIso = true)
            }
        } catch (se: SecurityException) {
            Log.e(TAG, "SecurityException in getDetailedEmergencyContacts", se)
            return getFallbackEmergencyContacts()
        } catch (e: Exception) {
            Log.e(TAG, "Unknown Exception in getDetailedEmergencyContacts", e)
            return getFallbackEmergencyContacts()
        }
    }

    private fun mapCategoryToTypeString(category: Int): String {
        return when (category) {
            EmergencyNumber.EMERGENCY_SERVICE_CATEGORY_UNSPECIFIED -> "Urgences (non spécifié)"
            EmergencyNumber.EMERGENCY_SERVICE_CATEGORY_POLICE -> "Police"
            EmergencyNumber.EMERGENCY_SERVICE_CATEGORY_AMBULANCE -> "Ambulance"
            EmergencyNumber.EMERGENCY_SERVICE_CATEGORY_FIRE_BRIGADE -> "Pompiers"
            EmergencyNumber.EMERGENCY_SERVICE_CATEGORY_MIEC -> "eCall (MIeC)"
            EmergencyNumber.EMERGENCY_SERVICE_CATEGORY_AIEC -> "eCall (AIeC)"
            else -> "Urgences (catégorie : $category)"
        }
    }

    // Fallback
    private fun getFallbackEmergencyContacts(useNetworkCountryIso: Boolean = false): List<EmergencyContact> {
        val contacts = mutableListOf<EmergencyContact>()
        // Add a general EU fallback first
        contacts.add(EmergencyContact(number = "112", type = "Emergency (General EU)"))

        if (useNetworkCountryIso && telephonyManager != null) {
            try {
                // Note: telephonyManager.getNetworkCountryIso() can sometimes return empty or null.
                val countryIso = telephonyManager.networkCountryIso?.uppercase()
                Log.d(TAG, "Fallback using Network Country ISO: $countryIso")
                when (countryIso) {
                    "US", "CA" -> contacts.add(
                        EmergencyContact(number = "911", type = "Emergency (USA/Canada)")
                    )
                    "GB" -> contacts.add(
                        EmergencyContact(number = "999", type = "Emergency (UK)")
                    )
                    "AU" -> contacts.add(
                        EmergencyContact(number = "000", type = "Emergency (Australia)")
                    )
                }
            } catch (se: SecurityException) {
                Log.e(TAG, "SecurityException getting networkCountryIso in fallback", se)
            } catch (e: Exception) {
                Log.e(TAG, "Unknown Exception getting networkCountryIso in fallback", e)
            }
        } else if (!useNetworkCountryIso) {
            // If not using network ISO, add a very common global number
            contacts.add(EmergencyContact(number = "911", type = "Emergency (general global)"))
        }
        return contacts.distinctBy { it.number }
    }

// ----- OLD METHODS, KEEPING THEM FOR NOW -----
//    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
//    fun getEmergencyNumbers(): List<String> {
//        if (ContextCompat.checkSelfPermission(context, READ_PHONE_STATE) != PERMISSION_GRANTED) {
//            Log.w(TAG, "READ_PHONE_STATE permission not granted for getEmergencyNumberList.")
//            // Fallback to common emergency numbers if permission is denied
//            return listOf("112", "911")
//        }
//
//        try {
//            // The map can contain different categories of emergency numbers
//            val emergencyNumbersMap = telephonyManager.emergencyNumberList
//
//            // Flatten the lists of EmergencyNumber objects from all categories
//            val allEmergencyNumbersAsObjects: List<EmergencyNumber> = emergencyNumbersMap.values.flatten()
//
//            // Map each EmergencyNumber object to its String representation (the number itself)
//            val emergencyNumberStrings: List<String> = allEmergencyNumbersAsObjects.map { emergencyNumber ->
//                emergencyNumber.number
//            }
//
//            // Get distinct numbers and then handle the ifEmpty case
//            return emergencyNumberStrings.distinct().ifEmpty {
//                Log.w(TAG, "TelephonyManager.emergencyNumberList returned empty or null." +
//                        "Falling back.")
//                getFallbackEmergencyNumbers()
//            }
//        } catch (se: SecurityException) {
//            Log.e(TAG, "SecurityException in getEmergencyNumbers", se)
//            return getFallbackEmergencyNumbers()
//        } catch (e: Exception) {
//            Log.e(TAG, "Unknown Exception in getEmergencyNumbers", e)
//            return getFallbackEmergencyNumbers()
//        }
//    }
//
//    private fun getFallbackEmergencyNumbers(): List<String> {
//        val numbers = mutableListOf<String>()
//        numbers.add("112") // EU
//
//        // Try to get country-specific numbers if possible
//        try {
//            val countryIso = telephonyManager.networkCountryIso?.uppercase()
//            Log.d(TAG, "Network Country ISO: $countryIso")
//            when (countryIso) {
//                "US", "CA" -> numbers.add("911") // US, Canada, US territories
//                "GB" -> numbers.add("999") // UK
//                "AU" -> numbers.add("000") // Australia
//            }
//        } catch (se: SecurityException) {
//            Log.e(TAG, "SecurityException getting networkCountryIso", se)
//        } catch (e: Exception) {
//            Log.e(TAG, "Unknown Exception getting networkCountryIso", e)
//        }
//        return numbers.distinct()
//    }

    companion object {
        private const val TAG = "SystemEmergencySource"
        private const val READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE
        private const val PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED
    }
}