package com.keak.aishou.notifications

import com.keak.aishou.data.UserSessionManager
import com.keak.aishou.utils.Platform
import com.keak.aishou.purchase.PlatformKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class OneSignalService(
    private val oneSignalManager: OneSignalManager,
    private val userSessionManager: UserSessionManager,
    private val scope: CoroutineScope
) {

    // OneSignal App ID is now centralized in PlatformKeys

    /**
     * Initialize OneSignal and set up user tracking
     */
    fun initialize() {
        oneSignalManager.initialize(PlatformKeys.oneSignalAppId)

        // Set up user tracking when OneSignal is ready
        scope.launch {
            setupUserTracking()
        }
    }

    /**
     * Request notification permission and log OneSignal ID
     */
    suspend fun requestPermissionAndRegister(): Boolean {
        val permissionGranted = oneSignalManager.requestNotificationPermission()

        if (permissionGranted) {
            logOneSignalId()
        }

        return permissionGranted
    }

    /**
     * Get OneSignal ID and log it
     */
    suspend fun logOneSignalId() {
        try {
            println("OneSignal: Starting to capture OneSignal ID on platform: ${getPlatform()}")

            val oneSignalId = oneSignalManager.getOneSignalId()
            val userId = userSessionManager.getUserId()

            println("OneSignal: Retrieved OneSignal ID: $oneSignalId")
            println("OneSignal: Retrieved User ID: $userId")

            if (oneSignalId != null && userId != null) {
                println("OneSignal: ✅ Successfully captured OneSignal ID: $oneSignalId for user: $userId")
                println("OneSignal: Platform: ${getPlatform()}")

                // Set external user ID for OneSignal
                oneSignalManager.setExternalUserId(userId)

                // Add user tags
                addUserTags()
            } else {
                println("OneSignal: ❌ Missing data - OneSignalId: $oneSignalId, UserId: $userId")
                if (oneSignalId == null) {
                    println("OneSignal: OneSignal ID is null - check OneSignal initialization")
                }
                if (userId == null) {
                    println("OneSignal: User ID is null - check UserSessionManager")
                }
            }
        } catch (e: Exception) {
            println("OneSignal: ❌ Error getting OneSignal ID: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Set up user tracking and log OneSignal ID
     */
    private suspend fun setupUserTracking() {
        println("OneSignal: Setting up user tracking on platform: ${getPlatform()}")

        // Wait a bit for OneSignal to initialize properly
        kotlinx.coroutines.delay(2000)

        println("OneSignal: Checking if user has been initialized...")
        val isFirstTime = userSessionManager.isUserFirstTime()
        println("OneSignal: Is first time user: $isFirstTime")

        val userId = userSessionManager.getUserId()
        println("OneSignal: Retrieved user ID from session: $userId")

        if (userId != null) {
            println("OneSignal: Setting external user ID: $userId")
            oneSignalManager.setExternalUserId(userId)
            logOneSignalId()
        } else {
            println("OneSignal: ⚠️ No user ID found - user might not be initialized yet")
            println("OneSignal: Triggering user initialization...")

            // Try to trigger user initialization if not done
            try {
                userSessionManager.handleAppStart()
                val newUserId = userSessionManager.getUserId()
                println("OneSignal: After initialization, user ID: $newUserId")

                if (newUserId != null) {
                    oneSignalManager.setExternalUserId(newUserId)
                    logOneSignalId()
                }
            } catch (e: Exception) {
                println("OneSignal: ❌ Error during user initialization: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * Add user tags based on app data
     */
    private suspend fun addUserTags() {
        val tags = mutableMapOf<String, String>()

        // Add launch count
        val launchCount = userSessionManager.getLaunchCount()
        tags["launch_count"] = launchCount.toString()

        // Add user type
        val isFirstTime = userSessionManager.isUserFirstTime()
        tags["user_type"] = if (isFirstTime) "new" else "returning"

        // Add platform
        tags["platform"] = getPlatform()

        oneSignalManager.addTags(tags)
    }

    /**
     * Get current platform
     */
    private fun getPlatform(): String {
        return Platform.name
    }
}