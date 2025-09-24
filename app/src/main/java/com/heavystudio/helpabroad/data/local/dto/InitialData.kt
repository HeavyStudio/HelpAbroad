package com.heavystudio.helpabroad.data.local.dto

import kotlinx.serialization.Serializable
import java.io.Serial

@Serializable
data class InitialData(
    val serviceTypes: List<JsonServiceType>,
    val countries: List<JsonCountry>
)

@Serializable
data class JsonServiceType(
    val code: String,
    val icon: String,
    val names: List<JsonTranslation>
)

@Serializable
data class JsonCountry(
    val isoCode: String,
    val names: List<JsonTranslation>,
    val services: List<JsonEmergencyServices>
)

@Serializable
data class JsonTranslation(
    val lang: String,
    val name: String
)

@Serializable
data class JsonEmergencyServices(
    val type: String,
    val number: String
)
