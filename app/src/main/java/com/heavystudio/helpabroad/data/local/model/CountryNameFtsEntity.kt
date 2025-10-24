package com.heavystudio.helpabroad.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

/**
 * Represents a full-text search (FTS) virtual table for country names in the local database.
 * This entity is used to perform efficient text searches on the `name` column of the
 * `CountryNameEntity`.
 *
 * The FTS4 table is configured with the `UNICODE61` tokenizer, which is suitable for
 * multi-language text. The `remove_diacritics=2` tokenizer argument ensures that searches are
 * case-insensitive and diacritic-insensitive (e.g., searching for "franc" will match "Français").
 *
 * @property name The name of the country. This column is indexed for full-text search.
 * @see CountryNameEntity The content entity that this FTS table is based on.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the entity.
 *
 */
@Entity(tableName = "country_names_fts")
@Fts4(
    contentEntity = CountryNameEntity::class,
    tokenizer = FtsOptions.TOKENIZER_UNICODE61,
    tokenizerArgs = ["remove_diacritics=2"]
)
data class CountryNameFtsEntity(
    @ColumnInfo(name = "name")
    val name: String
)

