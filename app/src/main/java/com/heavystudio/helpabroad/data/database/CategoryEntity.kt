package com.heavystudio.helpabroad.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name_res_key")
    val nameResKey: String,

    @ColumnInfo(name = "description_res_key")
    val descriptionResKey: String?,
) {

    override fun toString(): String {
        return """
            CategoryEntity(
                id=$id,
                nameResKey='$nameResKey',
                descriptionResKey=$descriptionResKey
            )
        """.trimIndent()
    }
}
