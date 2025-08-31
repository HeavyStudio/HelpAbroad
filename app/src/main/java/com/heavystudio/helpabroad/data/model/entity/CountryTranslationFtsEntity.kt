package com.heavystudio.helpabroad.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = CountryTranslationEntity::class)
@Entity(tableName = "country_translations_fts")
data class CountryTranslationFtsEntity(
    @ColumnInfo(name = "name")
    val name: String
)
