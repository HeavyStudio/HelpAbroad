package com.heavystudio.helpabroad.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "services",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["category_id"])]
)
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name_res_key")
    val nameResKey: String,

    @ColumnInfo(name = "icon")
    val icon: String?,

    @ColumnInfo(name = "category_id")
    val categoryId: Int?,

    @ColumnInfo(name = "notes_res_key")
    val notesResKey: String?,

    @ColumnInfo(name = "can_be_deleted")
    val canBeDeleted: Boolean
) {

    override fun toString(): String {
        return """
            ID: $id; 
            Name: $nameResKey; 
            ${icon?.let { "Icon: $it;" } ?: ""}
            ${categoryId?.let { "Category ID: $it;" } ?: ""}
            ${notesResKey?.let { "Notes: $it;" } ?: ""}
            Can be deleted: $canBeDeleted
        """.trimIndent()
    }
}
