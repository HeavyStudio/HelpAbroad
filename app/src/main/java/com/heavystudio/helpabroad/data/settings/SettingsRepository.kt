package com.heavystudio.helpabroad.data.settings

import android.content.Context
import androidx.compose.ui.input.key.Key
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Represents the available theme options for the application.
 *
 * This enum is used to manage the app's appearance, allowing the user to select
 * between a light theme, a dark theme, or to follow the system's theme setting.
 *
 * @property value The string representation of the theme, used for persistence in DataStore.
 */
enum class AppTheme(val value: String) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system");

    companion object {
        fun fromString(value: String?) = entries.find { it.value == value } ?: SYSTEM
    }
}

/**
 * Repository for managing application settings using Jetpack DataStore.
 *
 * This class provides a centralized way to read and write user preferences,
 * such as the app theme, calling behavior, and default country selection.
 * It exposes settings as `Flow`s, allowing UI components to reactively update
 * when a preference changes.
 *
 * @property context The application context, injected by Hilt, used to access the DataStore.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the repository.
 */
@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // Define the keys for each saved preference
    private object Keys {
        val APP_THEME = stringPreferencesKey("app_theme")
        val DIRECT_CALL = booleanPreferencesKey("direct_call")
        val CONFIRM_BEFORE_CALL = booleanPreferencesKey("confirm_before_call")
        val DEFAULT_COUNTRY_ID = intPreferencesKey("default_country_id")
        val RECENTLY_SEARCHED_COUNTRIES = stringPreferencesKey("recently_searched_countries")
    }

    // Expose the Flow for each preference
    val themeFlow = context.dataStore.data.map { preferences ->
        AppTheme.fromString(preferences[Keys.APP_THEME] ?: AppTheme.SYSTEM.value)
    }

    val directCallFlow = context.dataStore.data.map { preferences ->
        preferences[Keys.DIRECT_CALL] ?: false // Désactivé par défaut
    }

    val confirmBeforeCallFlow = context.dataStore.data.map { preferences ->
        preferences[Keys.CONFIRM_BEFORE_CALL] ?: true // Activé par défaut
    }

    val defaultCountryIdFlow = context.dataStore.data.map { preferences ->
        preferences[Keys.DEFAULT_COUNTRY_ID]
    }

    val recentlySearchedCountriesFlow = context.dataStore.data.map { preferences ->
        val idsString = preferences[Keys.RECENTLY_SEARCHED_COUNTRIES] ?: ""
        if (idsString.isBlank()) {
            emptyList()
        } else {
            idsString.split(",").mapNotNull { it.toIntOrNull() }
        }
    }

    /**
     * Persists the user's selected application theme.
     *
     * This function saves the chosen theme preference to DataStore, allowing it to be
     * retrieved across app sessions. The theme is stored as a string value.
     *
     * @param theme The [AppTheme] to be set and saved.
     */
    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { settings ->
            settings[Keys.APP_THEME] = theme.value
        }
    }

    /**
     * Sets the direct call preference.
     *
     * This function updates the DataStore to enable or disable the direct call feature.
     * When enabled, tapping a phone number will initiate a call immediately.
     *
     * @param isEnabled `true` to enable direct calling, `false` to disable it.
     */
    suspend fun setDirectCall(isEnabled: Boolean) {
        context.dataStore.edit { settings ->
            settings[Keys.DIRECT_CALL] = isEnabled
        }
    }

    /**
     * Sets the preference for confirming before making a call.
     *
     * This function updates the DataStore to enable or disable the confirmation dialog
     * that appears before initiating a call to an emergency number.
     *
     * @param isEnabled `true` to show a confirmation dialog before calling, `false` to disable it.
     */
    suspend fun setConfirmBeforeCall(isEnabled: Boolean) {
        context.dataStore.edit { settings ->
            settings[Keys.CONFIRM_BEFORE_CALL] = isEnabled
        }
    }

    /**
     * Adds a country's ID to the user's search history.
     *
     * This function is intended to store a list of recently viewed or searched countries,
     * allowing for quick access in the future. The implementation for storing and retrieving
     * this history is not yet defined in the provided code.
     *
     * @param countryId The unique identifier of the country to add to the history.
     */
    suspend fun addCountryToHistory(countryId: Int) {
        context.dataStore.edit { settings ->
            val currentIdsString = settings[Keys.RECENTLY_SEARCHED_COUNTRIES] ?: ""
            val currentIds = currentIdsString.split(",")
                .mapNotNull { it.toIntOrNull() }
                .toMutableList()

            // Remove the ID if it already exists to move it to the front.
            currentIds.remove(countryId)

            // Add the new ID to the beginning of the list.
            currentIds.add(0, countryId)

            // Keep only the 5 most recent IDs and convert back to a string.
            val newIdsString = currentIds.take(5).joinToString(",")
            settings[Keys.RECENTLY_SEARCHED_COUNTRIES] = newIdsString
        }
    }
}