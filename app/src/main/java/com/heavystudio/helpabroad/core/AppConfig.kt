package com.heavystudio.helpabroad.core

/**
 * A singleton object that holds application-wide configuration values.
 *
 * This object centralizes constants and settings that are used throughout the app,
 * making them easy to manage and access.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the file with supported languages.
 */
object AppConfig {
    val supportedLanguages = listOf(
        "de",
        "en",
        "es",
        "fr",
        "it",
        "pt"
    )
}