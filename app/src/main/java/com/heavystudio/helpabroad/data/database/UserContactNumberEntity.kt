package com.heavystudio.helpabroad.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_contact_numbers",
    foreignKeys = [
        ForeignKey(
            entity = CountryEntity::class,
            parentColumns = ["iso_code"],
            childColumns = ["country_iso_code"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ServiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["service_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["country_iso_code"]),
        Index(value = ["service_id"])
    ]
)
data class UserContactNumberEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "country_iso_code")
    val countryIsoCode: String,

    @ColumnInfo(name = "contact_name")
    val contactName: String,

    @ColumnInfo(name = "contact_number")
    val contactNumber: String,

    @ColumnInfo(name = "service_id")
    val serviceId: Int?,

    @ColumnInfo(name = "notes")
    val notes: String?
)