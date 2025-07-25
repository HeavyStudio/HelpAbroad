package com.heavystudio.helpabroad.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cat_categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "cat_id")
    val id: Int = 0,

    @ColumnInfo(name = "cat_name_res_key")
    val nameResKey: String?,

    @ColumnInfo(name = "cat_custom_name")
    val customName: String? = null,

    @ColumnInfo(name = "cat_is_predefined")
    val isPredefined: Boolean
)
