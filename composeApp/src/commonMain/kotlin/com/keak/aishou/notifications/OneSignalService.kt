package com.keak.aishou.notifications

import com.keak.aishou.data.UserSessionManager
import com.keak.aishou.data.DataStoreManager
import com.keak.aishou.data.api.PushReq
import com.keak.aishou.network.AishouApiService
import com.keak.aishou.network.ApiResult
import com.keak.aishou.utils.Platform
import com.keak.aishou.purchase.PlatformKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class OneSignalService(
    private val oneSignalManager: OneSignalManager,
    private val userSessionManager: UserSessionManager,
    private val dataStoreManager: DataStoreManager,
    private val apiService: AishouApiService,
    private val scope: CoroutineScope
) {

    // OneSignal App ID is now centralized in PlatformKeys
    private val logMutex = Mutex()
    private var scheduledRetryJob: Job? = null

    /**
     * Initialize OneSignal (without user tracking - that requires token)
     */
    fun initialize() {
        try {
            val appId = PlatformKeys.oneSignalAppId.takeIf { it.isNotBlank() }
            if (appId != null) {
                oneSignalManager.initialize(appId)
                println("OneSignal: ✅ Initialized with app ID: $appId")

                // Set up user state listener to automatically handle OneSignal ID changes
                setupUserStateListener()

                println("OneSignal: Waiting for auth token before user tracking...")
            } else {
                println("OneSignal: ❌ Invalid app ID, skipping initialization")
            }
        } catch (e: Exception) {
            println("OneSignal: ❌ Error during initialization: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Start user tracking after token is available
     */
    fun startUserTracking() {
        scope.launch(Dispatchers.Default) {
            println("OneSignal: Starting user tracking (token should be available now)")
            setupUserTracking()
        }
    }

    /**
     * Request notification permission only (without backend registration)
     */
    suspend fun requestNotificationPermission(): Boolean {
        return oneSignalManager.requestNotificationPermission()
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
     * Get OneSignal ID and log it with retry mechanism
     */
    suspend fun logOneSignalId() {
        logMutex.withLock {
            performLogOneSignalRegistration()
        }
    }

    /**
     * Retry getting OneSignal ID and User ID with exponential backoff
     */
    private suspend fun retryGetIds(maxAttempts: Int = 5): Pair<String?, String?> {
        repeat(maxAttempts) { attempt ->
            println("OneSignal: Attempt ${attempt + 1}/$maxAttempts to get IDs")

            val oneSignalId = oneSignalManager.getOneSignalId()?.takeIf { it.isNotBlank() }
            val userId = userSessionManager.getUserId()?.takeIf { it.isNotBlank() }

            println("OneSignal: Retrieved OneSignal ID: $oneSignalId")
            println("OneSignal: Retrieved User ID: $userId")

            if (oneSignalId != null && userId != null) {
                return Pair(oneSignalId, userId)
            }

            if (oneSignalId == null) {
                println("OneSignal: OneSignal ID is null - OneSignal might still be initializing")
            }
            if (userId == null) {
                println("OneSignal: User ID is null - UserSessionManager might not be ready")
            }

            if (attempt < maxAttempts - 1) {
                val delayMs = (1000L * (attempt + 1) * (attempt + 1)) // Exponential backoff: 1s, 4s, 9s, 16s
                println("OneSignal: Waiting ${delayMs}ms before next attempt...")
                delay(delayMs)
            }
        }

        return Pair(null, null)
    }

    /**
     * Set up user state listener to automatically handle OneSignal ID availability
     */
    private fun setupUserStateListener() {
        try {
            println("OneSignal: Setting up user state listener")
            oneSignalManager.addUserStateChangeListener { oneSignalId ->
                println("OneSignal: 🔄 User state changed - OneSignal ID: $oneSignalId")

                if (oneSignalId != null && oneSignalId.isNotBlank()) {
                    scope.launch(Dispatchers.Default) {
                        val userId = userSessionManager.getUserId()?.takeIf { it.isNotBlank() }
                        if (userId != null) {
                            println("OneSignal: ✅ Auto-registering OneSignal ID from state change: $oneSignalId")

                            // Add user tags
                            addUserTags()

                            // Send OneSignal ID to backend
                            registerOneSignalIdWithBackend(oneSignalId)
                        } else {
                            println("OneSignal: ⚠️ OneSignal ID available but no user ID yet")
                            scheduleRetry()
                        }
                    }
                } else {
                    println("OneSignal: ⚠️ User state change but OneSignal ID is still null")
                }
            }
        } catch (e: Exception) {
            println("OneSignal: ❌ Error setting up user state listener: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun performLogOneSignalRegistration() {
        cancelScheduledRetry()

        try {
            println("OneSignal: Starting to capture OneSignal ID on platform: ${getPlatform()}")
            withContext(Dispatchers.Default) {
                val (oneSignalId, userId) = retryGetIds()

                if (oneSignalId != null && userId != null) {
                    println("OneSignal: ✅ Successfully captured OneSignal ID: $oneSignalId for user: $userId")
                    println("OneSignal: Platform: ${getPlatform()}")

                    oneSignalManager.setExternalUserId(userId)
                    addUserTags()

                    scope.launch(Dispatchers.Default) {
                        registerOneSignalIdWithBackend(oneSignalId)
                    }
                } else {
                    println("OneSignal: ❌ Failed to get required IDs after retries - OneSignalId: $oneSignalId, UserId: $userId")
                    scheduleRetry()
                }
            }
        } catch (e: Exception) {
            println("OneSignal: ❌ Error getting OneSignal ID: ${e.message}")
            e.printStackTrace()
            scheduleRetry()
        }
    }

    private fun scheduleRetry(delayMillis: Long = 30000L) {
        if (scheduledRetryJob?.isActive == true) {
            println("OneSignal: Retry already scheduled, skipping new schedule")
            return
        }

        scheduledRetryJob = scope.launch(Dispatchers.Default) {
            delay(delayMillis)
            println("OneSignal: Retrying OneSignal ID capture after delay...")
            logOneSignalId()
        }
    }

    private fun cancelScheduledRetry() {
        if (scheduledRetryJob?.isActive == true) {
            println("OneSignal: Cancelling previously scheduled retry job")
            scheduledRetryJob?.cancel()
        }
        scheduledRetryJob = null
    }

    /**
     * Set up user tracking and log OneSignal ID
     */
    private suspend fun setupUserTracking() {
        println("OneSignal: Setting up user tracking on platform: ${getPlatform()}")

        // Wait longer for OneSignal to initialize properly on different platforms
        val initialDelay = if (getPlatform().lowercase() == "android") 3000L else 5000L
        delay(initialDelay)

        println("OneSignal: Checking if user has been initialized...")
        val isFirstTime = userSessionManager.isUserFirstTime()
        println("OneSignal: Is first time user: $isFirstTime")

        // Try to ensure we have a user ID
        var userId = userSessionManager.getUserId()?.takeIf { it.isNotBlank() }
        println("OneSignal: Retrieved user ID from session: $userId")

        if (userId == null) {
            println("OneSignal: ⚠️ No user ID found - triggering user initialization...")
            try {
                userSessionManager.handleAppStart()
                userId = userSessionManager.getUserId()?.takeIf { it.isNotBlank() }
                println("OneSignal: After initialization, user ID: $userId")
            } catch (e: Exception) {
                println("OneSignal: ❌ Error during user initialization: ${e.message}")
                e.printStackTrace()
            }
        }

        if (userId != null) {
            println("OneSignal: Setting external user ID: $userId")
            oneSignalManager.setExternalUserId(userId)

            // Wait a bit more after setting external user ID
            delay(1000)

            logOneSignalId()
        } else {
            println("OneSignal: ❌ Still no user ID after initialization - will retry later")

            // Schedule a retry
            scope.launch(Dispatchers.Default) {
                delay(10000) // Wait 10 seconds
                println("OneSignal: Retrying user tracking setup...")
                setupUserTracking()
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

            // Check if user has valid authentication token
            val accessToken = dataStoreManager.accessToken.first()
            if (accessToken.isNullOrBlank()) {
                println("OneSignal: ❌ No access token found - user may not be registered/authenticated")
                println("OneSignal: Skipping OneSignal registration until user is authenticated")
                return
            } else {
                println("OneSignal: ✅ Access token found, proceeding with OneSignal registration")
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
            val result = withContext(Dispatchers.Default) {
                apiService.registerPush(pushReq)
            }

            when (result) {
                is ApiResult.Success -> {
                    println("OneSignal: ✅ Successfully registered OneSignal ID with backend")

                    // Store the OneSignal ID to avoid duplicate registrations
                    dataStoreManager.setOneSignalId(oneSignalId)
                    println("OneSignal: OneSignal ID stored locally")
                }
                is ApiResult.Error -> {
                    println("OneSignal: ❌ Failed to register OneSignal ID: ${result.message}")
                    if (result.code == 401) {
                        println("OneSignal: ❌ 401 Unauthorized - Access token may be invalid or expired")
                        println("OneSignal: This usually means user registration failed or token wasn't saved properly")
                    }
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
