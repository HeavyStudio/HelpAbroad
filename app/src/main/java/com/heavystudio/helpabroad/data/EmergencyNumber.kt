package com.heavystudio.helpabroad.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "emergency_numbers",
    foreignKeys = [
        ForeignKey(
            entity = Country::class,
            parentColumns = ["countryCode"],
            childColumns = ["countryCode"],
            // If the country is deleted, its numbers are also deleted
            onDelete = ForeignKey.CASCADE
        )
    ],
    // Creates an index on the countryCode column for faster queries
    indices = [Index(value = ["countryCode"])]
)
data class EmergencyNumber(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val countryCode: String, // Foreign key referencing the Country entity
    val serviceName: String, // Name of the emergency service (e.g. "Police", "Ambulance")
    val phoneNumber: String, // Phone number of the emergency service (e.g. "17", "18", "112")
    val description: String? = null, // Optional description of the emergency service
    val iconResName: String? = null, // Optional: name of the drawable resource for the icon
    val category: String? = null // Optional: category of the emergency service
)
