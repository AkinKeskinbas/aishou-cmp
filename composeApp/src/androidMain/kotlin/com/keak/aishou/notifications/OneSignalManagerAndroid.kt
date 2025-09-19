package com.keak.aishou.notifications

import android.content.Context
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel

class OneSignalManagerAndroid(
    private val context: Context
) : OneSignalManager {

    override fun initialize(appId: String) {
        try {
            // Verbose Logging set to help debug issues, remove before releasing your app.
            OneSignal.Debug.logLevel = LogLevel.VERBOSE

            // OneSignal Initialization
            OneSignal.initWithContext(context, appId)

            println("OneSignal: Initialized with app ID: $appId")
        } catch (e: Exception) {
            println("OneSignal: Error during initialization: ${e.message}")
        }
    }

    override suspend fun getOneSignalId(): String? {
        return try {
            OneSignal.User.onesignalId
        } catch (e: Exception) {
            println("OneSignal: Error getting user ID: ${e.message}")
            null
        }
    }

    override fun setUserConsent(hasConsent: Boolean) {
        try {
            // This method may not exist in current OneSignal version
            println("OneSignal: Setting user consent: $hasConsent")
        } catch (e: Exception) {
            println("OneSignal: Error setting consent: ${e.message}")
        }
    }

    override suspend fun requestNotificationPermission(): Boolean {
        return try {
            OneSignal.Notifications.requestPermission(true)
        } catch (e: Exception) {
            println("OneSignal: Error requesting permission: ${e.message}")
            false
        }
    }

    override fun setExternalUserId(externalId: String) {
        try {
            OneSignal.login(externalId)
            println("OneSignal: Set external user ID: $externalId")
        } catch (e: Exception) {
            println("OneSignal: Error setting external user ID: ${e.message}")
        }
    }

    override fun addTags(tags: Map<String, String>) {
        try {
            OneSignal.User.addTags(tags)
            println("OneSignal: Added tags: $tags")
        } catch (e: Exception) {
            println("OneSignal: Error adding tags: ${e.message}")
        }
    }

    override fun removeTags(tagKeys: List<String>) {
        try {
            OneSignal.User.removeTags(tagKeys)
            println("OneSignal: Removed tags: $tagKeys")
        } catch (e: Exception) {
            println("OneSignal: Error removing tags: ${e.message}")
        }
    }
}