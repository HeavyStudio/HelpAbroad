package com.heavystudio.helpabroad.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pen_personal_emergency_numbers",
    foreignKeys = [
        ForeignKey(
            entity = CountryEntity::class,
            parentColumns = ["cnt_iso_code"],
            childColumns = ["pen_country_iso_code"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["cat_id"],
            childColumns = ["pen_category_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["pen_country_iso_code"], name = "idx_pen_country_iso_code"),
        Index(value = ["pen_category_id"], name = "idx_pen_category_id"),
        Index(value = ["pen_is_favorite"], name = "idx_pen_is_favorite")
    ]
)
data class PersonalEmergencyNumberEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pen_id")
    val id: Int = 0,

    // -- Fields always available
    @ColumnInfo(name = "pen_contact_name")
    val contactName: String,

    @ColumnInfo(name = "pen_phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "pen_relationship_description")
    val relationshipDescription: String? = null,

    @ColumnInfo(name = "pen_country_iso_code")
    val countryIsoCode: String,

    @ColumnInfo(name = "pen_creation_timestamp", defaultValue = "STRFTIME('%s', 'now')")
    val creationTimestamp: Long = System.currentTimeMillis() / 1000L,

    // -- Premium fields
    @ColumnInfo(name = "pen_category_id")
    val categoryId: Int? = null,

    @ColumnInfo(name = "pen_notes")
    val notes: String? = null,

    @ColumnInfo(name = "pen_is_favorite", defaultValue = "0")
    val isFavorite: Boolean = false


)
