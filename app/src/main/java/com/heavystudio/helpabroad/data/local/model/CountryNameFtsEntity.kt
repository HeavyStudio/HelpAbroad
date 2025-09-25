package com.heavystudio.helpabroad.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

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

