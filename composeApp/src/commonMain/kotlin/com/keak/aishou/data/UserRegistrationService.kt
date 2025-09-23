package com.keak.aishou.data

import com.keak.aishou.data.api.UserRegister
import com.keak.aishou.data.language.LanguageManager
import com.keak.aishou.network.AishouApiService
import com.keak.aishou.network.ApiResult
import com.keak.aishou.notifications.OneSignalService
import com.keak.aishou.purchase.PremiumChecker
import com.keak.aishou.utils.Platform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class UserRegistrationService(
    private val userSessionManager: UserSessionManager,
    private val dataStoreManager: DataStoreManager,
    private val apiService: AishouApiService,
    private val languageManager: LanguageManager,
    private val oneSignalService: OneSignalService,
    private val scope: CoroutineScope
) {
    private val initializationMutex = Mutex()
    private var initialized = false
    private val permissionMutex = Mutex()
    private var permissionRequested = false
    private var permissionJob: Job? = null

    suspend fun initialize() {
        initializationMutex.withLock {
            if (initialized) return
            withContext(Dispatchers.Default) {
                handleFirstAppOpen()
            }
            initialized = true
        }
    }

    private suspend fun handleFirstAppOpen() {
        try {
            println("UserRegistration: Checking if user needs registration...")

            val isFirstTime = userSessionManager.isUserFirstTime()
            val userId = userSessionManager.getUserId()

            if (isFirstTime || userId == null) {
                println("UserRegistration: First time user or no user ID - registering user...")
                registerUser()
            } else {
                println("UserRegistration: User already registered with ID: $userId")
                finishSetupForReturningUser()
            }
        } catch (e: Exception) {
            println("UserRegistration: Error during initialization: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun registerUser() {
        try {
            userSessionManager.handleAppStart()

            val userId = userSessionManager.ensureRevenueCatUserId()
            if (userId == null) {
                println("UserRegistration: ❌ Failed to get RevenueCat user ID")
                println("UserRegistration: Make sure RevenueCat is properly initialized")
                return
            }

            val currentLanguage = languageManager.currentLanguage.first()
            val languageCode = currentLanguage?.languageCode ?: "en"
            val platform = Platform.name.lowercase()

            val userRegister = UserRegister(
                revenueCatId = userId,
                displayName = null,
                photoUrl = null,
                lang = languageCode,
                platform = platform,
                isAnonymous = true,
                isPremium = PremiumChecker.isPremium
            )

            println("UserRegistration: Sending registration request...")
            println("UserRegistration: - RevenueCat ID: $userId")
            println("UserRegistration: - Language: $languageCode")
            println("UserRegistration: - Platform: $platform")

            when (val result = apiService.registerUser(userRegister)) {
                is ApiResult.Success -> {
                    println("UserRegistration: ✅ User registered successfully!")

                    result.data.data?.let { tokenResponse ->
                        dataStoreManager.setTokens(tokenResponse.token, tokenResponse.refreshToken)
                        println("UserRegistration: Tokens stored successfully")
                        println("UserRegistration: Access token: ${tokenResponse.token}")
                        println("UserRegistration: Refresh token: ${tokenResponse.refreshToken}")
                    } ?: println("UserRegistration: ❌ No token data in response!")

                    markUserAsRegistered()

                    scope.launch(Dispatchers.Main) {
                        finishSetupForReturningUser()
                    }
                }
                is ApiResult.Error -> {
                    println("UserRegistration: ❌ Registration failed: ${result.message}")
                    handleRegistrationFailure(Exception(result.message ?: "Unknown error"))
                }
                is ApiResult.Exception -> {
                    println("UserRegistration: ❌ Registration exception: ${result.exception.message}")
                    result.exception.printStackTrace()
                    handleRegistrationFailure(result.exception)
                }
            }

        } catch (e: Exception) {
            println("UserRegistration: ❌ Exception during registration: ${e.message}")
            e.printStackTrace()
            handleRegistrationFailure(e)
        }
    }

    private suspend fun finishSetupForReturningUser() {
        println("UserRegistration: Finishing setup for authenticated user")
        oneSignalService.startUserTracking()
        launchNotificationPermissionRequest()
    }

    private fun launchNotificationPermissionRequest() {
        if (permissionJob?.isActive == true) return

        permissionJob = scope.launch(Dispatchers.Main) {
            val shouldRequest = permissionMutex.withLock {
                if (permissionRequested) return@withLock false
                permissionRequested = true
                true
            }

            if (!shouldRequest) {
                permissionJob = null
                return@launch
            }

            println("UserRegistration: Requesting notification permission...")

            try {
                val permissionGranted = oneSignalService.requestPermissionAndRegister()
                println("UserRegistration: Notification permission granted: $permissionGranted")
                if (!permissionGranted) {
                    permissionMutex.withLock { permissionRequested = false }
                }
            } catch (e: Exception) {
                println("UserRegistration: Error requesting notification permission: ${e.message}")
                permissionMutex.withLock { permissionRequested = false }
            } finally {
                permissionJob = null
            }
        }
    }

    fun requestNotificationPermission() {
        launchNotificationPermissionRequest()
    }

    private suspend fun markUserAsRegistered() {
        try {
            dataStoreManager.markUserAsReturning()
            println("UserRegistration: User marked as registered in local storage")
        } catch (e: Exception) {
            println("UserRegistration: Error marking user as registered: ${e.message}")
        }
    }

    private suspend fun handleRegistrationFailure(exception: Throwable) {
        println("UserRegistration: Handling registration failure...")
        markUserAsRegistered()
        println("UserRegistration: Registration failed but marked user as registered to avoid retries")
        println("UserRegistration: Failure reason: ${exception.message}")
    }

    suspend fun forceReregister() {
        println("UserRegistration: Force re-registration requested")
        registerUser()
    }

    suspend fun isUserAuthenticated(): Boolean {
        val isFirstTime = userSessionManager.isUserFirstTime()
        val hasUserId = userSessionManager.getUserId() != null
        return !isFirstTime && hasUserId
    }
}
