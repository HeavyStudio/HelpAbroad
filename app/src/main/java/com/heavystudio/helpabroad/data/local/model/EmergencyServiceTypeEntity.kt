package com.heavystudio.helpabroad.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "emergency_service_types",
    indices = [Index(value = ["service_code"], unique = true)]
)
data class EmergencyServiceTypeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "service_code")
    val serviceCode: String,

    @ColumnInfo(name = "default_icon_ref")
    val defaultIconRef: String
)
