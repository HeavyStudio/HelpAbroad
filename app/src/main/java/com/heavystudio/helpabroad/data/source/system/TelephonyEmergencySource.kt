package com.heavystudio.helpabroad.data.source.system

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.telephony.emergency.EmergencyNumber
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.heavystudio.helpabroad.data.model.EmergencyCategoryKeys
import com.heavystudio.helpabroad.data.model.EmergencyContact
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelephonyEmergencySource @Inject constructor(
    private val telephonyManager: TelephonyManager,
    @param:ApplicationContext private val context: Context
) {
    val currentNetworkCountryIso: String? by lazy {
        try {
            telephonyManager.networkCountryIso?.uppercase()
        } catch (e: Exception) {
            Log.e(TAG, "Could not get networkCountryIso", e)
            null
        }
    }

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getDetailedEmergencyContacts(): List<EmergencyContact> {

        if (ContextCompat.checkSelfPermission(context, READ_PHONE_STATE) != PERMISSION_GRANTED) {
            Log.w(TAG, "READ_PHONE_STATE permission not granted for getDetailedEmergencyContacts." +
                    "Returning fallback emergency contacts.")
            return getFallbackEmergencyContacts()
        }

        // This list will be populated with ALL numbers found and categorized by TelephonyManager
        val allCategorizedContacts = mutableListOf<EmergencyContact>()

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
                    var typeKey = EmergencyCategoryKeys.UNSPECIFIED

                    if (specificCategoriesList.isNotEmpty()) {
                        var foundPreferredType = false
                        for (cat in specificCategoriesList) {
                            val mappedKey = mapCategoryToTypeKey(cat)
                            if (cat != EmergencyNumber.EMERGENCY_SERVICE_CATEGORY_UNSPECIFIED &&
                                mappedKey != EmergencyCategoryKeys.UNSPECIFIED &&
                                !mappedKey.startsWith(EmergencyCategoryKeys.UNKNOWN_CATEGORY_PREFIX)) {
                                typeKey = mappedKey
                                foundPreferredType = true
                                break
                            }
                        }
                        if (!foundPreferredType) {
                            typeKey = mapCategoryToTypeKey(specificCategoriesList[0])
                        }
                    } else {
                        Log.w(TAG, "Number ${emergencyNumberObject.number} has empty " +
                                "specificCategoriesList. Map group was $mapGroupCategory")
                    }

                    // --- SPECIAL HANDLING FOR INTERNATIONAL NUMBERS ---
                    if (typeKey == "Urgences (non spécifié)" || typeKey.startsWith("Urgences (catégorie:")) {
                        when (emergencyNumberObject.number) {
                            "112" -> typeKey = EmergencyCategoryKeys.EUROPE_GENERAL
                            "911" -> typeKey = EmergencyCategoryKeys.NORTH_AMERICA_GENERAL
                            "999" -> typeKey = EmergencyCategoryKeys.UK_GENERAL
                            "000" -> typeKey = EmergencyCategoryKeys.AUSTRALIA_GENERAL
                        }
                    }
                    // --- END OF SPECIAL HANDLING ---

                    // Log details for each number object
                    Log.d(TAG, "Processing Number: ${emergencyNumberObject.number}, \n" +
                            "Map Group Category: $mapGroupCategory, \n" +
                            "Specific Categories List: $specificCategoriesList, \n" +
                            "Mapped Type: $typeKey, \n" +
                            "URNs: ${emergencyNumberObject.emergencyUrns}, \n" +
                            "Sources: ${emergencyNumberObject.emergencyNumberSources}")

                    allCategorizedContacts.add(
                        EmergencyContact(
                            number = emergencyNumberObject.number,
                            typeKey = typeKey
                        )
                    )
                }
            }

            val uniqueCategorizedContacts = if (allCategorizedContacts.isNotEmpty()) {
                allCategorizedContacts.distinctBy { it.number }
            } else {
                emptyList()
            }

            if (uniqueCategorizedContacts.isEmpty()) {
                Log.w(TAG, "Processed emergencyNumberList but result is empty " +
                        "(before geographic filtering). Attempting fallback.")
                getFallbackEmergencyContacts(useNetworkCountryIso = true)
            }

            // --- FILTER BY GEOGRAPHIC RELEVANCE ---
            Log.d(TAG, "Before geographic filtering, contacts: " +
                    uniqueCategorizedContacts.joinToString { it.number })
            val geographicallyFilteredContacts = filterEmergencyContactsByRelevance(
                uniqueCategorizedContacts,
                currentNetworkCountryIso
            )
            Log.d(TAG, "After geographic filtering, contacts: ")

            return geographicallyFilteredContacts.ifEmpty {
                Log.w(
                    TAG, "Geographic filtering resulted in an empty list. " +
                            "Returning original unique categorized contacts to ensure something " +
                            "is shown."
                )
                uniqueCategorizedContacts
            }
        } catch (se: SecurityException) {
            Log.e(TAG, "SecurityException in getDetailedEmergencyContacts", se)
            return getFallbackEmergencyContacts()
        } catch (e: Exception) {
            Log.e(TAG, "Unknown Exception in getDetailedEmergencyContacts", e)
            return getFallbackEmergencyContacts()
        }
    }

    private fun mapCategoryToTypeKey(category: Int): String {
        return when (category) {
            EmergencyNumber.EMERGENCY_SERVICE_CATEGORY_UNSPECIFIED -> EmergencyCategoryKeys.UNSPECIFIED
            EmergencyNumber.EMERGENCY_SERVICE_CATEGORY_POLICE -> EmergencyCategoryKeys.POLICE
            EmergencyNumber.EMERGENCY_SERVICE_CATEGORY_AMBULANCE -> EmergencyCategoryKeys.AMBULANCE
            EmergencyNumber.EMERGENCY_SERVICE_CATEGORY_FIRE_BRIGADE -> EmergencyCategoryKeys.FIRE_DEPT
            EmergencyNumber.EMERGENCY_SERVICE_CATEGORY_MIEC -> EmergencyCategoryKeys.MIEC
            EmergencyNumber.EMERGENCY_SERVICE_CATEGORY_AIEC -> EmergencyCategoryKeys.AIEC
            else -> "${EmergencyCategoryKeys.UNKNOWN_CATEGORY_PREFIX}$category"
        }
    }

    // Fallback
    private fun getFallbackEmergencyContacts(useNetworkCountryIso: Boolean = false): List<EmergencyContact> {
        val contacts = mutableListOf<EmergencyContact>()
        // Add a general EU fallback first
        contacts.add(EmergencyContact(number = "112", typeKey = "Emergency (General EU)"))

        if (useNetworkCountryIso) {
            try {
                // Note: telephonyManager.getNetworkCountryIso() can sometimes return empty or null.
                val countryIso = telephonyManager.networkCountryIso?.uppercase()
                Log.d(TAG, "Fallback using Network Country ISO: $countryIso")
                when (countryIso) {
                    "US", "CA" -> contacts.add(
                        EmergencyContact(number = "911", typeKey = "Emergency (USA/Canada)")
                    )
                    "GB" -> contacts.add(
                        EmergencyContact(number = "999", typeKey = "Emergency (UK)")
                    )
                    "AU" -> contacts.add(
                        EmergencyContact(number = "000", typeKey = "Emergency (Australia)")
                    )
                }
            } catch (se: SecurityException) {
                Log.e(TAG, "SecurityException getting networkCountryIso in fallback", se)
            } catch (e: Exception) {
                Log.e(TAG, "Unknown Exception getting networkCountryIso in fallback", e)
            }
        } else {
            // If not using network ISO, add a very common global number
            contacts.add(EmergencyContact(number = "911", typeKey = "Emergency (general global)"))
        }
        return contacts.distinctBy { it.number }
    }

    private fun filterEmergencyContactsByRelevance(
        contacts: List<EmergencyContact>,
        currentCountryIso: String?
    ): List<EmergencyContact> {
        if (currentCountryIso.isNullOrBlank()) {
            Log.w(TAG, "Current country ISO is unknown for filtering, return all contacts.")
            return contacts
        }

        Log.d(TAG, "Filtering contacts for country $currentCountryIso. " +
                "Origin count: ${contacts.size}")

        val filteredList = contacts.filter { contact ->
            when (contact.number) {
                "911" -> currentCountryIso in listOf("US", "CA")
                "000" -> currentCountryIso == "AU"
                "999" -> currentCountryIso == "GB"
                "112" -> true
                else -> true
            }
        }
        Log.d(TAG, "Filtered list count: ${filteredList.size}")
        return filteredList
    }

    companion object {
        private const val TAG = "SystemEmergencySource"
        private const val READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE
        private const val PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED
    }
}