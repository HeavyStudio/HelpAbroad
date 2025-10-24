package com.heavystudio.helpabroad.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represents the localized name of a country in the local database.
 *
 * This entity stores the translation of a country's name for a specific language.
 * It is linked to a [CountryEntity] via a foreign key.
 *
 * @property id The unique identifier for this country name entry.
 * @property countryId The ID of the country this name belongs to. This is a foreign key to [CountryEntity.id].
 * @property languageCode The ISO 639-1 language code for this translation (e.g., "en", "es", "fr").
 * @property name The translated name of the country.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the entity.
 */
@Entity(
    tableName = "country_names",
    foreignKeys = [
        ForeignKey(
            entity = CountryEntity::class,
            parentColumns = ["id"],
            childColumns = ["country_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CountryNameEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "country_id", index = true)
    val countryId: Int,

    @ColumnInfo(name = "language_code")
    val languageCode: String,

    @ColumnInfo(name = "name")
    val name: String
)
