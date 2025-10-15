package com.heavystudio.helpabroad.ui.model

data class UiCountryDetails(
    val countryIsoCode: String,
    val countryName: String,
    val services: List<UiEmergencyService>
)

data class UiEmergencyService(
    val code: String,
    val name: String,
    val number: String
)