package com.heavystudio.helpabroad.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "emergency_numbers",
    foreignKeys = [
        ForeignKey(
            entity = CountryEntity::class,
            parentColumns = ["iso_code"],
            childColumns = ["iso_code"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = ServiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["service_id"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        )
    ],
    indices = [
        Index(value = ["iso_code"]),
        Index(value = ["service_id"])
    ]
)
data class EmergencyNumberEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "iso_code")
    val isoCode: String,

    @ColumnInfo(name = "number")
    val number: String,

    @ColumnInfo(name = "service_id")
    val serviceId: Long,

    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long
)
