package com.example.demochatapplication.features.shared.usersettings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.demochatapplication.features.shared.usersettings.UserSettingsSerializer.Companion.USER_SETTINGS_FILE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.datastore by preferencesDataStore(USER_SETTINGS_FILE_NAME)

@Singleton()
class UserSettingsRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
) {
    private val datastore = appContext.datastore

    val userSettings: Flow<UserSettings> = datastore.data.catch {
        if (it is IOException) {
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map {
        mapPreferencesToUserSettings(it)
    }

    suspend fun getFirstEntry(): UserSettings = mapPreferencesToUserSettings(datastore.data.first())

    suspend fun writeUsername(username: String) {
        datastore.edit {
            it[PreferencesKeys.KEY_USERNAME] = username
        }
    }

    suspend fun writePassword(password: String) {
        datastore.edit {
            it[PreferencesKeys.KEY_PASSWORD] = password
        }
    }

    suspend fun writeToken(token: String) {
        datastore.edit {
            it[PreferencesKeys.KEY_TOKEN] = token
        }
    }

    suspend fun writeUserSettings(userSettings: UserSettings) {
        datastore.edit {
            it[PreferencesKeys.KEY_TOKEN] = userSettings.token
            it[PreferencesKeys.KEY_PASSWORD] = userSettings.password
            it[PreferencesKeys.KEY_USERNAME] = userSettings.username
        }
    }
    private fun mapPreferencesToUserSettings(preferences: Preferences): UserSettings {
        val username = preferences[PreferencesKeys.KEY_USERNAME] ?:""
        val password = preferences[PreferencesKeys.KEY_PASSWORD] ?: ""
        val token = preferences[PreferencesKeys.KEY_TOKEN] ?: ""

        return UserSettings(username = username, password = password, token = token)
    }

    private object PreferencesKeys{
        val KEY_USERNAME = stringPreferencesKey("username")
        val KEY_PASSWORD = stringPreferencesKey("password")
        val KEY_TOKEN = stringPreferencesKey("token")
    }
}