package com.keak.aishou.notifications

import com.keak.aishou.data.UserSessionManager
import com.keak.aishou.data.DataStoreManager
import com.keak.aishou.data.api.PushReq
import com.keak.aishou.network.AishouApiService
import com.keak.aishou.network.ApiResult
import com.keak.aishou.utils.Platform
import com.keak.aishou.purchase.PlatformKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OneSignalService(
    private val oneSignalManager: OneSignalManager,
    private val userSessionManager: UserSessionManager,
    private val dataStoreManager: DataStoreManager,
    private val apiService: AishouApiService,
    private val scope: CoroutineScope
) {

    // OneSignal App ID is now centralized in PlatformKeys

    /**
     * Initialize OneSignal and set up user tracking
     */
    fun initialize() {
        try {
            val appId = PlatformKeys.oneSignalAppId.takeIf { it.isNotBlank() }
            if (appId != null) {
                oneSignalManager.initialize(appId)
                println("OneSignal: ✅ Initialized with app ID: $appId")

                // Set up user tracking when OneSignal is ready
                scope.launch {
                    setupUserTracking()
                }
            } else {
                println("OneSignal: ❌ Invalid app ID, skipping initialization")
            }
        } catch (e: Exception) {
            println("OneSignal: ❌ Error during initialization: ${e.message}")
            e.printStackTrace()
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

            val oneSignalId = oneSignalManager.getOneSignalId()?.takeIf { it.isNotBlank() }
            val userId = userSessionManager.getUserId()?.takeIf { it.isNotBlank() }

            println("OneSignal: Retrieved OneSignal ID: $oneSignalId")
            println("OneSignal: Retrieved User ID: $userId")

            if (oneSignalId != null && userId != null) {
                println("OneSignal: ✅ Successfully captured OneSignal ID: $oneSignalId for user: $userId")
                println("OneSignal: Platform: ${getPlatform()}")

                // Set external user ID for OneSignal
                oneSignalManager.setExternalUserId(userId)

                // Add user tags
                addUserTags()

                // Send OneSignal ID to backend
                scope.launch {
                    registerOneSignalIdWithBackend(oneSignalId)
                }
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

        val userId = userSessionManager.getUserId()?.takeIf { it.isNotBlank() }
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
                val newUserId = userSessionManager.getUserId()?.takeIf { it.isNotBlank() }
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
     * Register OneSignal ID with backend
     */
    private suspend fun registerOneSignalIdWithBackend(oneSignalId: String) {
        try {
            println("OneSignal: Registering OneSignal ID with backend...")

            // Check if this ID is already registered
            val storedOneSignalId = dataStoreManager.oneSignalId.first()
            if (storedOneSignalId == oneSignalId) {
                println("OneSignal: ✅ OneSignal ID already registered and unchanged: $oneSignalId")
                return
            }

            // Get current locale and timezone - simplified for cross-platform compatibility
            val locale = "en_US" // Default locale for now
            val timezone = "UTC" // Default timezone for now

            // Create push registration request
            val pushReq = PushReq(
                playerId = oneSignalId,
                platform = getPlatform().lowercase(),
                locale = locale,
                timezone = timezone
            )

            println("OneSignal: Sending push registration request...")
            println("OneSignal: - Player ID: $oneSignalId")
            println("OneSignal: - Platform: ${getPlatform().lowercase()}")
            println("OneSignal: - Locale: $locale")
            println("OneSignal: - Timezone: $timezone")

            // Send request to backend
            val result = apiService.registerPush(pushReq)

            when (result) {
                is ApiResult.Success -> {
                    println("OneSignal: ✅ Successfully registered OneSignal ID with backend")

                    // Store the OneSignal ID to avoid duplicate registrations
                    dataStoreManager.setOneSignalId(oneSignalId)
                    println("OneSignal: OneSignal ID stored locally")
                }
                is ApiResult.Error -> {
                    println("OneSignal: ❌ Failed to register OneSignal ID: ${result.message}")
                }
                is ApiResult.Exception -> {
                    println("OneSignal: ❌ Exception registering OneSignal ID: ${result.exception.message}")
                    result.exception.printStackTrace()
                }
            }

        } catch (e: Exception) {
            println("OneSignal: ❌ Error registering OneSignal ID with backend: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Check and update OneSignal ID if changed
     */
    suspend fun checkAndUpdateOneSignalId() {
        try {
            println("OneSignal: Checking for OneSignal ID changes...")

            val currentOneSignalId = oneSignalManager.getOneSignalId()?.takeIf { it.isNotBlank() }
            val storedOneSignalId = dataStoreManager.oneSignalId.first()

            if (currentOneSignalId != null) {
                if (storedOneSignalId != currentOneSignalId) {
                    println("OneSignal: 🔄 OneSignal ID changed from $storedOneSignalId to $currentOneSignalId")
                    registerOneSignalIdWithBackend(currentOneSignalId)
                } else {
                    println("OneSignal: ✅ OneSignal ID unchanged: $currentOneSignalId")
                }
            } else {
                println("OneSignal: ⚠️ No current OneSignal ID available")
            }

        } catch (e: Exception) {
            println("OneSignal: ❌ Error checking OneSignal ID: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Get current platform
     */
    private fun getPlatform(): String {
        return Platform.name
    }
}