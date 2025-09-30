package com.heavystudio.helpabroad.data.local.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.heavystudio.helpabroad.data.local.model.CountryEntity
import com.heavystudio.helpabroad.data.local.model.CountryNameEntity
import com.heavystudio.helpabroad.data.local.model.EmergencyNumberEntity

data class CountryDetails(
    @Embedded
    val country: CountryEntity,

    @Relation(
        entity = CountryNameEntity::class,
        parentColumn = "id",
        entityColumn = "country_id"
    )
    val names: List<CountryNameEntity>,

    @Relation(
        entity = EmergencyNumberEntity::class,
        parentColumn = "id",
        entityColumn = "country_id"
    )
    val services: List<EmergencyServiceDetails>
)
