package com.heavystudio.helpabroad.ui.common

fun isoCodeToFlagEmoji(isoCode: String): String {
    return isoCode
        .uppercase()
        .map { char ->
            Character.toString(char.code + 0x1F1A5)
        }
        .joinToString("")
}