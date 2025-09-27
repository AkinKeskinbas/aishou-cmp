package com.keak.aishou.data

import com.keak.aishou.data.language.LanguageManager
import com.keak.aishou.notifications.OneSignalService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Central service for initializing all app services on startup
 */
class AppInitializationService(
    private val languageManager: LanguageManager,
    private val userRegistrationService: UserRegistrationService,
    private val userSessionManager: UserSessionManager,
    private val oneSignalService: OneSignalService,
    private val scope: CoroutineScope
) {

    /**
     * Initialize all app services in the correct order
     */
    fun initialize() {
        scope.launch {
            println("AppInitialization: Starting app initialization...")

            try {
                // Step 1: Initialize language system
                println("AppInitialization: 1/5 - Initializing language system...")
                languageManager.initialize()

                // Step 2: Initialize user registration FIRST (to get auth token)
                println("AppInitialization: 2/5 - Initializing user registration...")
                userRegistrationService.initialize()

                // Step 3: Small delay to let user registration complete
                delay(2000)

                // Step 4: Initialize OneSignal (after we have auth token) - Non-blocking
                println("AppInitialization: 3/5 - Initializing OneSignal...")
                scope.launch {
                    try {
                        oneSignalService.initialize()

                        // Small delay then check OneSignal ID
                        delay(1000)
                        oneSignalService.checkAndUpdateOneSignalId()

                        println("AppInitialization: OneSignal initialization completed in background")
                    } catch (e: Exception) {
                        println("AppInitialization: OneSignal initialization failed: ${e.message}")
                    }
                }

                // Step 5: Skip automatic permission request - will be handled in HomeScreen
                println("AppInitialization: 4/5 - OneSignal started in background...")
                println("AppInitialization: 5/5 - Skipping automatic permission request (handled in HomeScreen)...")

                println("AppInitialization: ✅ All services initialized successfully")

            } catch (e: Exception) {
                println("AppInitialization: ❌ Error during initialization: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * Re-initialize specific services (useful for testing or recovery)
     */
    suspend fun reinitializeUserRegistration() {
        userRegistrationService.forceReregister()
    }

    /**
     * Check if user needs re-authentication and return appropriate result
     */
    suspend fun checkAuthStatus(): AuthStatus {
        return try {
            if (userSessionManager.hasValidAuthentication()) {
                AuthStatus.Authenticated
            } else {
                if (userSessionManager.shouldAttemptReauth()) {
                    AuthStatus.NeedsReauth
                } else {
                    AuthStatus.ReauthBlocked
                }
            }
        } catch (e: Exception) {
            println("AppInitialization: Error checking auth status: ${e.message}")
            AuthStatus.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Handle unauthorized user response
     */
    suspend fun handleUnauthorizedUser(): Boolean {
        return try {
            userSessionManager.handleUnauthorizedUser()
            true
        } catch (e: Exception) {
            println("AppInitialization: Error handling unauthorized user: ${e.message}")
            false
        }
    }
}

sealed class AuthStatus {
    object Authenticated : AuthStatus()
    object NeedsReauth : AuthStatus()
    object ReauthBlocked : AuthStatus()
    data class Error(val message: String) : AuthStatus()
}
