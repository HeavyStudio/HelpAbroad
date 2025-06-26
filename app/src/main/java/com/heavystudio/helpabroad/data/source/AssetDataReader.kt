package com.heavystudio.helpabroad.data.source

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.heavystudio.helpabroad.data.Country
import java.io.IOException

data class CountryJson(
    @SerializedName("countryCode") val countryCode: String,
    @SerializedName("countryNameResName") val countryName: String,
    @SerializedName("flagEmoji") val flagEmoji: String?,
    @SerializedName("internationalEmergencyNumber") val internationalEmergencyNumber: String?
)

class AssetDataReader(private val context: Context) {

    fun readCountriesFromJson(fileName: String): List<Country> {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use {
                it.readText()
            }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return emptyList()
        }

        val listType = object : TypeToken<List<Country>>() {}.type
        val countriesJson = Gson().fromJson<List<CountryJson>>(jsonString, listType)

        // Convert CountryJson objects to Country entities, resolving resource IDs
        return countriesJson.map { countryJson ->
            val resourceId = context.resources.getIdentifier(
                countryJson.countryName,
                "string",
                context.packageName
            )
            Country(
                countryCode = countryJson.countryCode,
                countryName = resourceId,
                flagEmoji = countryJson.flagEmoji,
                internationalEmergencyNumber = countryJson.internationalEmergencyNumber
            )
        }
    }
}