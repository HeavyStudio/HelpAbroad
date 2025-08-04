package com.heavystudio.helpabroad.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface OnboardingInterface {
    fun isOnboardingComplete(): Flow<Boolean>
    suspend fun setOnboardingComplete(isComplete: Boolean)
}

internal object PreferenceKeys {
    val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
}

@Singleton
class OnboardingRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : OnboardingInterface {

    override fun isOnboardingComplete(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PreferenceKeys.ONBOARDING_COMPLETE] ?: false
            }
    }

    override suspend fun setOnboardingComplete(isComplete: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.ONBOARDING_COMPLETE] = isComplete
        }
    }

    // For testing, remove in prod
    suspend fun resetOnboarding() {
        setOnboardingComplete(false)
    }
}