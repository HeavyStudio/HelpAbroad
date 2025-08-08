package com.heavystudio.helpabroad.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
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
        Log.d("ONBOARDING_REPO", "isOnboardingComplete() Flow accessed")
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val completed = preferences[PreferenceKeys.ONBOARDING_COMPLETE] ?: false
                Log.d("PERMISSIONS_VM_REPO", "DataStore: Read ONBOARDING_COMPLETE as: $completed")
                completed
            }
            .distinctUntilChanged()
    }

    override suspend fun setOnboardingComplete(isComplete: Boolean) {
        Log.d("PERMISSIONS_VM_REPO", "setOnboardingComplete($isComplete) CALLED") // Step 2 Log
        try {
            dataStore.edit { settings ->
                settings[PreferenceKeys.ONBOARDING_COMPLETE] = isComplete
                Log.d("PERMISSIONS_VM_REPO", "DataStore: Set ONBOARDING_COMPLETE to $isComplete") // Step 2 Log
            }
            Log.d("PERMISSIONS_VM_REPO", "DataStore edit successful for $isComplete") // Step 2 Log
        } catch (e: Exception) {
            Log.e("PERMISSIONS_VM_REPO", "DataStore edit FAILED for $isComplete", e) // Step 2 Log for errors
        }
    }

    // For testing, remove in prod
    suspend fun resetOnboarding() {
        setOnboardingComplete(false)
    }
}