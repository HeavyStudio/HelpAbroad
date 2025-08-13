package com.heavystudio.helpabroad.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name_res_key")
    val nameResKey: String
) {

    override fun toString(): String {
        return """
            ServiceEntity(
                id=$id,
                nameResKey=$nameResKey
            )
        """.trimIndent()
    }
}
