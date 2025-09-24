package com.heavystudio.helpabroad.data.model.result

import androidx.room.Embedded
import com.heavystudio.helpabroad.data.model.entity.CountryEntity

data class CountryWithLocalizedName(
    @Embedded
    val country: CountryEntity,
    val localizedName: String,
    val regionName: String
)
