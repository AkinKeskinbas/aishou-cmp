package com.keak.aishou.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val IS_FIRST_TIME_USER = booleanPreferencesKey("is_first_time_user")
        private val USER_ID = stringPreferencesKey("user_id")
        private val FIRST_LAUNCH_TIMESTAMP = longPreferencesKey("first_launch_timestamp")
        private val APP_LAUNCH_COUNT = longPreferencesKey("app_launch_count")
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
}