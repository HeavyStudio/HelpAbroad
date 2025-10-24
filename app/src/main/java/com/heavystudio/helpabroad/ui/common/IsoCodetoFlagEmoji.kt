package com.heavystudio.helpabroad.ui.common

/**
 * Converts a two-letter ISO 3166-1 alpha-2 country code into a corresponding flag emoji.
 *
 * This function takes a two-character string representing a country code (e.g., "US", "JP")
 * and transforms it into the Unicode flag sequence for that country. It works by converting
 * each character of the country code to its corresponding "Regional Indicator Symbol".
 *
 * For example, "US" becomes "🇺🇸".
 *
 * @param isoCode The two-letter, case-insensitive ISO 3166-1 alpha-2 country code.
 * @return A string containing the flag emoji. If the input is not a valid two-letter
 *         code, the output may not render as a valid flag.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the component.
 */
fun isoCodeToFlagEmoji(isoCode: String): String {
    return isoCode
        .uppercase()
        .map { char ->
            Character.toString(char.code + 0x1F1A5)
        }
        .joinToString("")
}