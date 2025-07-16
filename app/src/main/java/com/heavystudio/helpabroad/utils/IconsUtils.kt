package com.heavystudio.helpabroad.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

object IconsUtils {

    fun getIconForType(type: String): ImageVector {
        return when {
            type.contains("Police", ignoreCase = true) -> Icons.Default.LocalPolice
            type.contains("Ambulance", ignoreCase = true) -> Icons.Default.LocalHospital
            type.contains("Fire", ignoreCase = true) -> Icons.Default.LocalFireDepartment
            else -> Icons.Default.Warning
        }
    }

    fun getFlagEmojiForCountryCode(countryCode: String?): String {
        if (countryCode.isNullOrBlank() || countryCode.length != 2) {
            return ""
        }

        val firstLetter =
            Character.codePointAt(countryCode.uppercase(), 0) - 'A'.code + 0x1F1E6
        val secondLetter =
            Character.codePointAt(countryCode.uppercase(), 1) - 'A'.code + 0x1F1E6

        val firstChar = String(Character.toChars(firstLetter))
        val secondChar = String(Character.toChars(secondLetter))

        return firstChar + secondChar
    }
}