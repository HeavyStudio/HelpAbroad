package com.heavystudio.helpabroad.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represents the localized name for an emergency service type in the local database.
 *
 * This entity stores the translation of a service type's name for a specific language.
 * It is linked to an `EmergencyServiceTypeEntity` via a foreign key relationship on `serviceTypeId`.
 * This allows for supporting multiple languages within the application for service type names.
 *
 * @property id The unique identifier for this name entry.
 * @property serviceTypeId The foreign key referencing the ID of the `EmergencyServiceTypeEntity`
 *                         this name belongs to.
 * @property languageCode The ISO 639-1 language code for this translation (e.g., "en", "es").
 * @property name The translated name of the service type.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the entity.
 */
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
