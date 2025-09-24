package com.heavystudio.helpabroad.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "emergency_numbers",
    foreignKeys = [
        ForeignKey(
            entity = CountryEntity::class,
            parentColumns = ["id"],
            childColumns = ["country_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EmergencyServiceTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["service_type_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EmergencyNumberEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "country_id", index = true)
    val countryId: Int,

    @ColumnInfo(name = "service_type_id", index = true)
    val serviceTypeId: Int,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "description")
    val description: String? = null
)
