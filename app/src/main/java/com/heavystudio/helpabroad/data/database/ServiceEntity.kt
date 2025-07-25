package com.heavystudio.helpabroad.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sen_services",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["cat_id"],
            childColumns = ["sen_category_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["sen_category_id"], name = "idx_sen_category_id")
    ]
)
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sen_id")
    val id: Int = 0,

    @ColumnInfo(name = "sen_name_res_key")
    val serviceNameResKey: String,

    @ColumnInfo(name = "sen_emoji")
    val serviceEmoji: String?,

    @ColumnInfo(name = "sen_category_id")
    val categoryId: Int
)
