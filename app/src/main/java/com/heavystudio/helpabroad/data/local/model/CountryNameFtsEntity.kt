package com.heavystudio.helpabroad.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "country_names_fts")
@Fts4(contentEntity = CountryNameEntity::class)
data class CountryNameFtsEntity(
    @ColumnInfo(name = "name")
    val name: String
)

