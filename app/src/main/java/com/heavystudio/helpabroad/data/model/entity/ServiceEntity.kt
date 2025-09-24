package com.heavystudio.helpabroad.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo("name_res_key")
    val nameResKey: String,

    @ColumnInfo("notes_res_key")
    val notesResKey: String?,

    @ColumnInfo("icon_res_key")
    val iconResKey: String?
)
