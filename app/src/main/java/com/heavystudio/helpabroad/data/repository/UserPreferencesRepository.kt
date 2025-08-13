package com.heavystudio.helpabroad.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val tag = "UserPreferencesRepository"

    private object PreferencesKeys {
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    }

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            // Default to true if the key doesn't exist (first launch)
            preferences[PreferencesKeys.IS_FIRST_LAUNCH] ?: true
        }
        .catch { exception ->
            if (exception is IOException) {
                Log.e(tag, "Error reading preferences.", exception)
                emit(true)
            } else {
                throw exception
            }
        }

    suspend fun updateIsFirstLaunch(isFirstLaunch: Boolean) {
        try {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.IS_FIRST_LAUNCH] = isFirstLaunch
            }
        } catch (e: IOException) {
            Log.e("UserPreferencesRepository", "An IO exception occurred while updating the preference.", e)
        } catch (e: Exception) {
            Log.e("UserPreferencesRepository", "An exception occurred while updating the preference.", e)
        }
    }
}