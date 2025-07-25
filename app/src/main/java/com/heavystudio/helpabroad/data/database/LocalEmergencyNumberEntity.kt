package com.heavystudio.helpabroad.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "len_local_emergency_numbers",
    foreignKeys = [
        ForeignKey(
            entity = CountryEntity::class,
            parentColumns = ["cnt_iso_code"],
            childColumns = ["len_country_iso_code"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ServiceEntity::class,
            parentColumns = ["sen_id"],
            childColumns = ["len_service_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["len_country_iso_code"], name = "idx_len_country_iso_code"),
        Index(value = ["len_service_id"], name = "idx_len_service_id")
    ]
)
data class LocalEmergencyNumberEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "len_id")
    val id: Int = 0,

    @ColumnInfo(name = "len_country_iso_code")
    val countryIsoCode: String,

    @ColumnInfo(name = "len_number")
    val number: String,

    @ColumnInfo(name = "len_service_id")
    val serviceId: Int,

)
