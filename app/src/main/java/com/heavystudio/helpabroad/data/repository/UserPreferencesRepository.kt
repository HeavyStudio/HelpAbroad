package com.heavystudio.helpabroad.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.heavystudio.helpabroad.ui.viewmodel.state.PermissionStatus
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
        val IS_PERMISSIONS_SETUP_COMPLETED = booleanPreferencesKey("is_permissions_setup_completed")
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

    val isPermissionsSetupCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_PERMISSIONS_SETUP_COMPLETED] ?: false
        }
        .catch { exception ->
            if (exception is IOException) {
                Log.e(tag, "Error reading preferences", exception)
                emit(false)
            } else {
                throw exception
            }
        }

    fun getSavedPermissionStatus(permission: String): Flow<PermissionStatus> {
        val permissionStatusKey = stringPreferencesKey("permission_status_$permission")
        return context.dataStore.data
            .map { preferences ->
                when (preferences[permissionStatusKey]) {
                    "GRANTED" -> PermissionStatus.GRANTED
                    "DENIED_PERMANENTLY" -> PermissionStatus.DENIED_PERMANENTLY
                    else -> PermissionStatus.UNKNOWN
                }
            }
            .catch { exception ->
                if (exception is IOException) {
                    Log.e(tag, "Error reading preferences", exception)
                    emit(PermissionStatus.UNKNOWN)
                } else {
                    throw exception
                }
            }
    }

    suspend fun updateIsFirstLaunch(isFirstLaunch: Boolean) {
        try {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.IS_FIRST_LAUNCH] = isFirstLaunch
            }
        } catch (e: IOException) {
            Log.e(tag, "An IO exception occurred while updating the preference.", e)
        } catch (e: Exception) {
            Log.e(tag, "An exception occurred while updating the preference.", e)
        }
    }

    suspend fun updateIsPermissionsSetupCompleted(isPermissionsSetupComplete: Boolean) {
        try {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.IS_PERMISSIONS_SETUP_COMPLETED] = isPermissionsSetupComplete
            }
        } catch (e: IOException) {
            Log.e(tag, "An IO exception occurred while updating the preference.", e)
        } catch (e: Exception) {
            Log.e(tag, "An exception occurred while updating the preference.", e)
        }
    }

    suspend fun updatePermissionStatus(permission: String, status: PermissionStatus) {
        val statusToStore = when (status) {
            PermissionStatus.GRANTED -> "GRANTED"
            PermissionStatus.DENIED_PERMANENTLY -> "DENIED_PERMANENTLY"
            else -> null
        }

        if (statusToStore != null) {
            val permissionStatusKey = stringPreferencesKey("permission_status_$permission")
            try {
                context.dataStore.edit { preferences ->
                    preferences[permissionStatusKey] = statusToStore
                }
            } catch (e: IOException) {
                Log.e(tag, "An IO exception occurred while updating the preference.", e)
            } catch (e: Exception) {
                Log.e(tag, "An exception occurred while updating the preference.", e)
            }
        } else {
            val permissionStatusKey = stringPreferencesKey("permission_status_$permission")
            try {
                context.dataStore.edit { preferences ->
                    preferences.remove(permissionStatusKey)
                }
            } catch (e: IOException) {
                Log.e(tag, "An IO exception occurred while updating the preference.", e)
            } catch (e: Exception) {
                Log.e(tag, "An exception occurred while updating the preference.", e)
            }
        }
    }
}