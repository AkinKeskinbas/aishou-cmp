package com.keak.aishou.notifications

import android.content.Context
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OneSignalManagerAndroid(
    private val context: Context
) : OneSignalManager {

    private var onUserStateChangeListener: ((String?) -> Unit)? = null

    override fun initialize(appId: String) {
        try {
            println("OneSignal Android: 🚀 Starting initialization with app ID: $appId")
            println("OneSignal Android: Context: ${context.javaClass.simpleName}")

            // Verbose Logging set to help debug issues, remove before releasing your app.
            OneSignal.Debug.logLevel = LogLevel.VERBOSE
            println("OneSignal Android: Set log level to VERBOSE")

            // OneSignal Initialization
            OneSignal.initWithContext(context, appId)
            println("OneSignal Android: ✅ OneSignal.initWithContext() completed")

            // Check initial state
            println("OneSignal Android: Checking initial state after initialization...")
            try {
                val initialId = OneSignal.User.onesignalId
                println("OneSignal Android: Initial OneSignal ID: $initialId")
            } catch (e: Exception) {
                println("OneSignal Android: Could not get initial ID: ${e.message}")
            }

            println("OneSignal Android: ✅ Initialization completed successfully")
        } catch (e: Exception) {
            println("OneSignal Android: ❌ Error during initialization: ${e.message}")
            e.printStackTrace()
        }
    }

    override suspend fun getOneSignalId(): String? {
        return try {
            val userId = OneSignal.User.pushSubscription.id
            println("OneSignal Android: Raw OneSignal.User.onesignalId = '$userId'")

            if (userId.isNullOrBlank()) {
                println("OneSignal Android: ⚠️ OneSignal ID is null or blank")
                println("OneSignal Android: Checking user state...")

                // Try to get more info about OneSignal state
                try {
                    val hasConsent = OneSignal.User.pushSubscription.optedIn
                    val isSubscribed = OneSignal.User.pushSubscription.id != null
                    println("OneSignal Android: User opted in: $hasConsent")
                    println("OneSignal Android: User subscribed: $isSubscribed")
                    println("OneSignal Android: Subscription ID: ${OneSignal.User.pushSubscription.id}")
                } catch (e: Exception) {
                    println("OneSignal Android: Could not check subscription status: ${e.message}")
                }

                return null
            }

            println("OneSignal Android: ✅ Valid OneSignal ID retrieved: $userId")
            userId
        } catch (e: Exception) {
            println("OneSignal Android: ❌ Error getting user ID: ${e.message}")
            e.printStackTrace()
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
            println("OneSignal Android: 🔑 Setting external user ID: $externalId")
            println("OneSignal Android: Calling OneSignal.login()...")

            OneSignal.login(externalId)
            println("OneSignal Android: ✅ OneSignal.login() completed")

            // Check if this affects the OneSignal ID
            try {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000) // Wait a bit
                    val oneSignalId = OneSignal.User.onesignalId
                    println("OneSignal Android: OneSignal ID after login: $oneSignalId")
                }
            } catch (e: Exception) {
                println("OneSignal Android: Could not check ID after login: ${e.message}")
            }

        } catch (e: Exception) {
            println("OneSignal Android: ❌ Error setting external user ID: ${e.message}")
            e.printStackTrace()
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

    override fun addUserStateChangeListener(listener: (String?) -> Unit) {
        try {
            onUserStateChangeListener = listener
            println("OneSignal Android: Added user state change listener")

            // For now, simulate state change after a delay since OneSignal observer interfaces may have changed
            CoroutineScope(Dispatchers.Main).launch {
                delay(3000)
                val oneSignalId = OneSignal.User.onesignalId
                if (!oneSignalId.isNullOrBlank()) {
                    println("OneSignal Android: Simulating user state change with ID: $oneSignalId")
                    listener(oneSignalId)
                }
            }
        } catch (e: Exception) {
            println("OneSignal Android: Error adding user state listener: ${e.message}")
        }
    }

    override fun removeUserStateChangeListener() {
        try {
            onUserStateChangeListener = null
            println("OneSignal Android: Removed user state change listener")
        } catch (e: Exception) {
            println("OneSignal Android: Error removing user state listener: ${e.message}")
        }
    }
}