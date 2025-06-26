package com.heavystudio.helpabroad.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class Country(
    @PrimaryKey val countryCode: String,                // ISO 3166-1 alpha-2 code (e.g. "FR")
    val countryName: Int,                               // Country name (e.g. "France")
    val flagEmoji: String? = null,                      // Optional: flag emoji (e.g. "🇫🇷")
    val internationalEmergencyNumber: String? = null,   // Optional: international emergency number (e.g. "112")
    val isDownloaded: Boolean = false                   // Indicates if emergency numbers are downloaded
)
