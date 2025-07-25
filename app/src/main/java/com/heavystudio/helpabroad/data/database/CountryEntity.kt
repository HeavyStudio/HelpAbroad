package com.heavystudio.helpabroad.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cnt_countries")
data class CountryEntity(
    @PrimaryKey
    @ColumnInfo(name = "cnt_iso_code")
    val isoCode: String,

    @ColumnInfo(name = "cnt_name")
    val name: String,

    @ColumnInfo(name = "cnt_flag_emoji")
    val flagEmoji: String?,

    @ColumnInfo(name = "cnt_dial_code")
    val dialCode: String?,

    @ColumnInfo(name = "cnt_regional_dispatch")
    val regionalDispatch: String?,
)
