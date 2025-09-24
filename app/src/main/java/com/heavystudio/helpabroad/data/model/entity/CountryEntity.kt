package com.heavystudio.helpabroad.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "countries",
    foreignKeys = [
        ForeignKey(
            entity = RegionEntity::class,
            parentColumns = ["id"],
            childColumns = ["region_id"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        )
    ],
    indices = [Index(value = ["region_id"])]
)
data class CountryEntity(
    @PrimaryKey
    @ColumnInfo(name = "iso_code")
    val isoCode: String,

    @ColumnInfo(name = "flag")
    val flag: String,

    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long,

    @ColumnInfo(name = "region_id")
    val regionId: Long
)
