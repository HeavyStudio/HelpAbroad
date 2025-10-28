package com.heavystudio.helpabroad.data.local.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.heavystudio.helpabroad.data.local.model.EmergencyNumberEntity
import com.heavystudio.helpabroad.data.local.model.EmergencyServiceTypeEntity
import com.heavystudio.helpabroad.data.local.model.ServiceTypeNameEntity

/**
 * Represents a detailed view of an emergency service, combining information from multiple database
 * tables.
 *
 * This data class is used by Room to fetch a complete emergency service entry. It aggregates
 * an [EmergencyNumberEntity] with its corresponding [EmergencyServiceTypeEntity] and a list of
 * localized [ServiceTypeNameEntity]s. This structure is ideal for displaying comprehensive
 * service details to the user, including the number, the type of service, and its name in
 * various languages.
 *
 * @property number The core emergency number entity, embedded directly into this object.
 * @property type The type of the emergency service (e.g., Police, Ambulance, Fire). This is fetched
 *           by matching `EmergencyNumberEntity.service_type_id` with `EmergencyServiceTypeEntity.id`.
 * @property names A list of localized names for the service type (e.g., "Police", "Polizei", "Policía").
 *           This is fetched by matching `EmergencyNumberEntity.service_type_id` with
 *           `ServiceTypeNameEntity.service_type_id`.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the data class.
 */
data class EmergencyServiceDetails(
    @Embedded
    val number: EmergencyNumberEntity,

    @Relation(
        parentColumn = "service_type_id",
        entityColumn = "id"
    )
    val type: EmergencyServiceTypeEntity,

    @Relation(
        parentColumn = "service_type_id",
        entityColumn = "service_type_id"
    )
    val names: List<ServiceTypeNameEntity>
)
