package com.heavystudio.helpabroad.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "country_translations")
data class CountryTranslationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "iso_code")
    val isoCode: String,

    @ColumnInfo(name = "language_code")
    val languageCode: String,

    @ColumnInfo(name = "name")
    val name: String
)
