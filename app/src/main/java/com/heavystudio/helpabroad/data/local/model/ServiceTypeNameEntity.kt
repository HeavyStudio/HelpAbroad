package com.heavystudio.helpabroad.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "service_type_names",
    foreignKeys = [
        ForeignKey(
            entity = EmergencyServiceTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["service_type_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ServiceTypeNameEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "service_type_id", index = true)
    val serviceTypeId: Int,

    @ColumnInfo(name = "language_code")
    val languageCode: String,

    @ColumnInfo(name = "name")
    val name: String
)
