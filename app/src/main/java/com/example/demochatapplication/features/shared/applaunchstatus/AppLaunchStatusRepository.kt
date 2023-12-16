package com.example.demochatapplication.features.shared.applaunchstatus

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.datastore by preferencesDataStore(AppLaunchStatusRepository.APP_LAUNCH_STATUS_DATASTORE_FILE_NAME)
@Singleton
class AppLaunchStatusRepository @Inject constructor(
    @ApplicationContext appContext: Context
) {
    private val datastore = appContext.datastore

    val appLaunchStatusFlow = datastore
        .data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            }
            else {
                throw it
            }
        }
        .map {
            mapPreferencesToAppLaunchStatus(it)
        }

    suspend fun getFirstEntry(): AppLaunchStatus = mapPreferencesToAppLaunchStatus(datastore.data.first())

    suspend fun updateAppLaunchStatus (appLaunchStatus: AppLaunchStatus) {
        datastore.edit { appLaunchStatusPreferences->
            appLaunchStatusPreferences[PreferencesKeys.KEY_LAST_CHECKED] = appLaunchStatus.lastTimePermissionCheckedMillis
        }
    }

    private fun mapPreferencesToAppLaunchStatus(preferences: Preferences): AppLaunchStatus {
        val lastTimePermissionCheckedMillis = preferences[PreferencesKeys.KEY_LAST_CHECKED]?: 0

        return AppLaunchStatus(lastTimePermissionCheckedMillis = lastTimePermissionCheckedMillis)
    }

    companion object {
        const val APP_LAUNCH_STATUS_DATASTORE_FILE_NAME = "app_launch_status"
    }
    private object PreferencesKeys {
        val KEY_LAST_CHECKED = longPreferencesKey("lastTimePermissionCheckedMillis")
    }
}