package com.heavystudio.helpabroad.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey
    @ColumnInfo(name = "iso_code")
    val isoCode: String,

    @ColumnInfo(name = "flag")
    val flag: String
)
