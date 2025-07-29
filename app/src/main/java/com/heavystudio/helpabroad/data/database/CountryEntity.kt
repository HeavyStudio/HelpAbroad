package com.heavystudio.helpabroad.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "iso_code")
    val isoCode: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "flag_emoji")
    val flagEmoji: String?,

    @ColumnInfo(name = "iso_numeric")
    val isoNumeric: String,

    @ColumnInfo(name = "region_res_key")
    val regionResKey: String,

    @ColumnInfo(name = "member_112")
    val member112: Boolean,

    @ColumnInfo(name = "member_911")
    val member911: Boolean,

    @ColumnInfo(name = "country_code")
    val countryCode: String
) {

    override fun toString(): String {
        return """
            ISO Code: $isoCode; 
            Name: $name; 
            ${flagEmoji?.let { "Flag Emoji: $it" } ?: ""}
            ISO Numeric: $isoNumeric; 
            Region: $regionResKey; 
            Member 112: $member112; 
            Member 911: $member911; 
            Country Code: $countryCode
        """.trimIndent()
    }
}
