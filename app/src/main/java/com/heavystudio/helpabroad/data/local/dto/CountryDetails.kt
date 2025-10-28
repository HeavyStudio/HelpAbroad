package com.heavystudio.helpabroad.data.local.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.heavystudio.helpabroad.data.local.model.CountryEntity
import com.heavystudio.helpabroad.data.local.model.CountryNameEntity
import com.heavystudio.helpabroad.data.local.model.EmergencyNumberEntity

/**
 * Represents the detailed information for a single country, aggregating related entities.
 *
 * This data class is used by Room to fetch a `CountryEntity` along with its associated
 * `CountryNameEntity` (alternative names) and `EmergencyServiceDetails` (emergency services
 * and their numbers). The `@Relation` annotations define the one-to-many relationships
 * between the parent `CountryEntity` and its child entities.
 *
 * @property country The core country data, embedded directly into this object.
 * @property names A list of alternative names or translations for the country.
 * @property services A list of emergency services available in the country, each with their
 *                    respective numbers.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the data class.
 */
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
