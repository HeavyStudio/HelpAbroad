package com.heavystudio.helpabroad.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey
    @ColumnInfo(name = "iso_code")
    val isoCode: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "flag_emoji")
    val flagEmoji: String?,

    @ColumnInfo(name = "member_112")
    val member112: Boolean,

    @ColumnInfo(name = "member_911")
    val member911: Boolean
) {

    override fun toString(): String {
        return """
            CountryEntity(
                isoCode='$isoCode',
                name='$name',
                flagEmoji=$flagEmoji,
                member112=$member112,
                member911=$member911
            )
        """.trimIndent()
    }
}
