package com.keak.aishou.data

import com.keak.aishou.data.api.UserRegister
import com.keak.aishou.data.language.LanguageManager
import com.keak.aishou.network.AishouApiService
import com.keak.aishou.network.ApiResult
import com.keak.aishou.utils.Platform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Service responsible for handling user registration when the app first opens
 */
class UserRegistrationService(
    private val userSessionManager: UserSessionManager,
    private val dataStoreManager: DataStoreManager,
    private val apiService: AishouApiService,
    private val languageManager: LanguageManager,
    private val scope: CoroutineScope
) {

    /**
     * Initialize registration - should be called on app startup
     */
    fun initialize() {
        scope.launch {
            handleFirstAppOpen()
        }
    }

    /**
     * Handle registration logic for first app open
     */
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
            }
        } catch (e: Exception) {
            println("UserRegistration: Error during initialization: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Register user with backend
     */
    private suspend fun registerUser() {
        try {
            // Ensure user session is initialized first
            userSessionManager.handleAppStart()

            // Try to ensure we have a RevenueCat user ID
            val userId = userSessionManager.ensureRevenueCatUserId()
            if (userId == null) {
                println("UserRegistration: ❌ Failed to get RevenueCat user ID")
                println("UserRegistration: Make sure RevenueCat is properly initialized")
                return
            }

            // Get current language
            val currentLanguage = languageManager.currentLanguage.first()
            val languageCode = currentLanguage?.languageCode ?: "en"

            // Get platform
            val platform = Platform.name.lowercase()

            // Create registration request
            val userRegister = UserRegister(
                revenueCatId = userId,
                displayName = null, // Anonymous user
                photoUrl = null,
                lang = languageCode,
                platform = platform,
                isAnonymous = true
            )

            println("UserRegistration: Sending registration request...")
            println("UserRegistration: - API Endpoint: http://localhost:3060/v1/auth/register")
            println("UserRegistration: - RevenueCat ID: $userId")
            println("UserRegistration: - Language: $languageCode")
            println("UserRegistration: - Platform: $platform")
            println("UserRegistration: - Display Name: ${userRegister.displayName}")
            println("UserRegistration: - Photo URL: ${userRegister.photoUrl}")
            println("UserRegistration: - Is Anonymous: ${userRegister.isAnonymous}")
            println("UserRegistration: - Full request object: $userRegister")

            val result = apiService.registerUser(userRegister)

            when (result) {
                is ApiResult.Success -> {
                    println("UserRegistration: ✅ User registered successfully!")
                    println("UserRegistration: Response: ${result.data}")

                    // Store tokens from registration response
                    result.data.data?.let { tokenResponse ->
                        dataStoreManager.setTokens(tokenResponse.token, tokenResponse.refreshToken)
                        println("UserRegistration: Tokens stored successfully")
                    }

                    markUserAsRegistered()
                }
                is ApiResult.Error -> {
                    println("UserRegistration: ❌ Registration failed: ${result.message}")
                    handleRegistrationFailure(Exception(result.message ?: "Unknown error"))
                }
                is ApiResult.Exception -> {
                    val exception = result.exception
                    println("UserRegistration: ❌ Registration exception:")
                    println("UserRegistration: - Exception type: ${exception::class.simpleName}")
                    println("UserRegistration: - Exception message: ${exception.message}")
                    println("UserRegistration: - Exception cause: ${exception.cause}")
                    exception.printStackTrace()
                    handleRegistrationFailure(exception)
                }
            }

        } catch (e: Exception) {
            println("UserRegistration: ❌ Exception during registration:")
            println("UserRegistration: - Exception type: ${e::class.simpleName}")
            println("UserRegistration: - Exception message: ${e.message}")
            println("UserRegistration: - Exception cause: ${e.cause}")
            println("UserRegistration: - Stack trace:")
            e.printStackTrace()
            handleRegistrationFailure(e)
        }
    }

    /**
     * Mark user as registered in local storage
     */
    private suspend fun markUserAsRegistered() {
        try {
            // Update DataStore to mark user as no longer first time
            dataStoreManager.markUserAsReturning()
            println("UserRegistration: User marked as registered in local storage")
        } catch (e: Exception) {
            println("UserRegistration: Error marking user as registered: ${e.message}")
        }
    }

    /**
     * Handle registration failure
     */
    private suspend fun handleRegistrationFailure(exception: Throwable) {
        println("UserRegistration: Handling registration failure...")

        // For now, we'll still mark the user as registered to avoid repeated failed attempts
        // In a production app, you might want to implement retry logic or store failure state
        markUserAsRegistered()

        // Log the failure for debugging
        println("UserRegistration: Registration failed but marked user as registered to avoid retries")
        println("UserRegistration: Failure reason: ${exception.message}")
    }

    /**
     * Force re-registration (useful for testing or user profile updates)
     */
    suspend fun forceReregister() {
        println("UserRegistration: Force re-registration requested")
        registerUser()
    }

    /**
     * Check if user is registered
     */
    suspend fun isUserRegistered(): Boolean {
        val isFirstTime = userSessionManager.isUserFirstTime()
        val hasUserId = userSessionManager.getUserId() != null
        return !isFirstTime && hasUserId
    }
}