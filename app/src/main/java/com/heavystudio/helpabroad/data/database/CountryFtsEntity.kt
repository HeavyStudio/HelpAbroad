package com.heavystudio.helpabroad.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Fts4(languageId = "language_id")
@Entity(
    tableName = "countries_fts",
    foreignKeys = [
        ForeignKey(
            entity = CountryEntity::class,
            parentColumns = ["iso_code"],
            childColumns = ["country_iso_code"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CountryFtsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val id: Int,

    @ColumnInfo(name = "country_iso_code")
    val countryIsoCode: String,

    @ColumnInfo(name = "searchable_name")
    val searchableName: String,

    @ColumnInfo(name = "language_id")
    val languageId: String
)
