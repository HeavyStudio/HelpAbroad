package com.heavystudio.helpabroad.data.settings

import android.content.Context
import androidx.compose.ui.input.key.Key
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class AppTheme(val value: String) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system");

    companion object {
        fun fromString(value: String?) = entries.find { it.value == value } ?: SYSTEM
    }
}

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // Définition des "clés" pour chaque préférence sauvegardée
    private object Keys {
        val APP_THEME = stringPreferencesKey("app_theme")
        val DIRECT_CALL = booleanPreferencesKey("direct_call")
        val CONFIRM_BEFORE_CALL = booleanPreferencesKey("confirm_before_call")
    }

    // Exposition du Flow pour chaque préférence
    val themeFlow = context.dataStore.data.map { preferences ->
        AppTheme.fromString(preferences[Keys.APP_THEME] ?: AppTheme.SYSTEM.value)
    }

    val directCallFlow = context.dataStore.data.map { preferences ->
        preferences[Keys.DIRECT_CALL] ?: false // Désactivé par défaut
    }

    val confirmBeforeCallFlow = context.dataStore.data.map { preferences ->
        preferences[Keys.CONFIRM_BEFORE_CALL] ?: true // Activé par défaut
    }

    // Modification des préférences
    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { settings ->
            settings[Keys.APP_THEME] = theme.value
        }
    }

    suspend fun setDirectCall(isEnabled: Boolean) {
        context.dataStore.edit { settings ->
            settings[Keys.DIRECT_CALL] = isEnabled
        }
    }

    suspend fun setConfirmBeforeCall(isEnabled: Boolean) {
        context.dataStore.edit { settings ->
            settings[Keys.CONFIRM_BEFORE_CALL] = isEnabled
        }
    }
}