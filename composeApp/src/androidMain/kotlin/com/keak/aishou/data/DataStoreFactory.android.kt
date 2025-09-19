package com.keak.aishou.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.keak.aishou.AishouApplication

actual object DataStoreFactory {
    actual fun createDataStore(): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { AishouApplication.instance.preferencesDataStoreFile("aishou_preferences") }
        )
    }
}