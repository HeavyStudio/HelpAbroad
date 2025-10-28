package com.heavystudio.helpabroad.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a type of emergency service stored in the local database.
 *
 * This entity is used to define the different categories of emergency services available,
 * such as police, fire, or ambulance. Each service type has a unique code and a reference
 * to its default icon.
 *
 * @property id The unique auto-generated identifier for the emergency service type entry.
 * @property serviceCode A unique string identifier for the service (e.g., "police", "fire_department").
 *                       This is used to uniquely identify the service type.
 * @property defaultIconRef A reference or identifier (e.g., a drawable resource name) for the default
 *                          icon associated with this service type.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the entity.
 */
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
