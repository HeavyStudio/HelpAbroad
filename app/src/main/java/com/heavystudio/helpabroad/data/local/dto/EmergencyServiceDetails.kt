package com.heavystudio.helpabroad.data.local.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.heavystudio.helpabroad.data.local.model.EmergencyNumberEntity
import com.heavystudio.helpabroad.data.local.model.EmergencyServiceTypeEntity
import com.heavystudio.helpabroad.data.local.model.ServiceTypeNameEntity

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
