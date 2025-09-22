package com.keak.aishou.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.keak.aishou.data.language.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val IS_FIRST_TIME_USER = booleanPreferencesKey("is_first_time_user")
        private val USER_ID = stringPreferencesKey("user_id")
        private val FIRST_LAUNCH_TIMESTAMP = longPreferencesKey("first_launch_timestamp")
        private val APP_LAUNCH_COUNT = longPreferencesKey("app_launch_count")
        private val APP_LANGUAGE_CODE = stringPreferencesKey("app_language_code")
        private val APP_LANGUAGE_COUNTRY = stringPreferencesKey("app_language_country")
        private val APP_LANGUAGE_DISPLAY_NAME = stringPreferencesKey("app_language_display_name")
        private val APP_LANGUAGE_LOCALE = stringPreferencesKey("app_language_locale")
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val ONE_SIGNAL_ID = stringPreferencesKey("one_signal_id")
    }

    val isFirstTimeUser: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_FIRST_TIME_USER] ?: true
    }

    val userId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    val firstLaunchTimestamp: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[FIRST_LAUNCH_TIMESTAMP]
    }

    val appLaunchCount: Flow<Long> = dataStore.data.map { preferences ->
        preferences[APP_LAUNCH_COUNT] ?: 0L
    }

    val appLanguage: Flow<AppLanguage?> = dataStore.data.map { preferences ->
        val languageCode = preferences[APP_LANGUAGE_CODE]
        val countryCode = preferences[APP_LANGUAGE_COUNTRY]
        val displayName = preferences[APP_LANGUAGE_DISPLAY_NAME]
        val locale = preferences[APP_LANGUAGE_LOCALE]

        if (languageCode != null && displayName != null && locale != null) {
            AppLanguage(
                languageCode = languageCode,
                countryCode = countryCode,
                displayName = displayName,
                locale = locale
            )
        } else {
            null
        }
    }

    val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN]
    }

    val refreshToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN]
    }

    val oneSignalId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ONE_SIGNAL_ID]
    }


    suspend fun markUserAsReturning() {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_TIME_USER] = false
        }
    }

    suspend fun setUserId(id: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = id
        }
    }

    suspend fun setFirstLaunchTimestamp(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH_TIMESTAMP] = timestamp
        }
    }

    suspend fun incrementLaunchCount() {
        dataStore.edit { preferences ->
            val currentCount = preferences[APP_LAUNCH_COUNT] ?: 0L
            preferences[APP_LAUNCH_COUNT] = currentCount + 1
        }
    }

    suspend fun initializeFirstTimeUser(userId: String, timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_TIME_USER] = false
            preferences[USER_ID] = userId
            preferences[FIRST_LAUNCH_TIMESTAMP] = timestamp
            preferences[APP_LAUNCH_COUNT] = 1L
        }
    }

    suspend fun setAppLanguage(language: AppLanguage) {
        dataStore.edit { preferences ->
            preferences[APP_LANGUAGE_CODE] = language.languageCode
            language.countryCode?.let { preferences[APP_LANGUAGE_COUNTRY] = it }
            preferences[APP_LANGUAGE_DISPLAY_NAME] = language.displayName
            preferences[APP_LANGUAGE_LOCALE] = language.locale
        }
    }

    suspend fun clearAppLanguage() {
        dataStore.edit { preferences ->
            preferences.remove(APP_LANGUAGE_CODE)
            preferences.remove(APP_LANGUAGE_COUNTRY)
            preferences.remove(APP_LANGUAGE_DISPLAY_NAME)
            preferences.remove(APP_LANGUAGE_LOCALE)
        }
    }

    suspend fun setAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
        }
    }

    suspend fun setRefreshToken(token: String) {
        dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN] = token
        }
    }

    suspend fun setTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }

    suspend fun setOneSignalId(id: String) {
        dataStore.edit { preferences ->
            preferences[ONE_SIGNAL_ID] = id
        }
    }


    suspend fun clearOneSignalId() {
        dataStore.edit { preferences ->
            preferences.remove(ONE_SIGNAL_ID)
        }
    }
}