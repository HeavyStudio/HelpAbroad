package com.heavystudio.helpabroad.data.source

import com.heavystudio.helpabroad.data.Country

object EuropeanCountriesData {

    fun getEuropeanCountriesForInitialLoad(): List<Country> {
        val euEmergencyNum = "112"

        return listOf(
            Country(countryCode = "AT", countryName = "Autriche", flagEmoji = "🇦🇹", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "BE", countryName = "Belgique", flagEmoji = "🇧🇪", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "BG", countryName = "Bulgarie", flagEmoji = "🇧🇬", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "HR", countryName = "Croatie", flagEmoji = "🇭🇷", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "CY", countryName = "Chypre", flagEmoji = "🇨🇾", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "CZ", countryName = "Tchéquie", flagEmoji = "🇨🇿", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "DK", countryName = "Danemark", flagEmoji = "🇩🇰", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "EE", countryName = "Estonie", flagEmoji = "🇪🇪", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "FI", countryName = "Finlande", flagEmoji = "🇫🇮", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "FR", countryName = "France", flagEmoji = "🇫🇷", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "DE", countryName = "Allemagne", flagEmoji = "🇩🇪", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "GR", countryName = "Grèce", flagEmoji = "🇬🇷", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "HU", countryName = "Hongrie", flagEmoji = "🇭🇺", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "IE", countryName = "Irlande", flagEmoji = "🇮🇪", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "IT", countryName = "Italie", flagEmoji = "🇮🇹", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "LV", countryName = "Lettonie", flagEmoji = "🇱🇻", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "LT", countryName = "Lituanie", flagEmoji = "🇱🇹", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "LU", countryName = "Luxembourg", flagEmoji = "🇱🇺", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "MT", countryName = "Malte", flagEmoji = "🇲🇹", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "NL", countryName = "Pays-Bas", flagEmoji = "🇳🇱", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "PL", countryName = "Pologne", flagEmoji = "🇵🇱", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "PT", countryName = "Portugal", flagEmoji = "🇵🇹", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "RO", countryName = "Roumanie", flagEmoji = "🇷🇴", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "SK", countryName = "Slovaquie", flagEmoji = "🇸🇰", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "SI", countryName = "Slovénie", flagEmoji = "🇸🇮", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "ES", countryName = "Espagne", flagEmoji = "🇪🇸", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "SE", countryName = "Suède", flagEmoji = "🇸🇪", internationalEmergencyNumber = euEmergencyNum),

            // Non-EU countries often visited by Europeans, using 112
            Country(countryCode = "GB", countryName = "Royaume-Uni", flagEmoji = "🇬🇧", internationalEmergencyNumber = euEmergencyNum),
            Country(countryCode = "CH", countryName = "Suisse", flagEmoji = "🇨🇭", internationalEmergencyNumber = euEmergencyNum)
        )
    }
}