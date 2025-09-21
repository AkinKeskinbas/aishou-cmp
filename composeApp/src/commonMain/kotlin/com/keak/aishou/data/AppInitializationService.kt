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
                println("AppInitialization: 1/4 - Initializing language system...")
                languageManager.initialize()

                // Step 2: Initialize OneSignal
                println("AppInitialization: 2/4 - Initializing OneSignal...")
                oneSignalService.initialize()

                // Step 3: Small delay to let other systems settle
                delay(1000)

                // Step 4: Initialize user registration (depends on language and user session)
                println("AppInitialization: 3/5 - Initializing user registration...")
                userRegistrationService.initialize()

                // Step 5: Check and update OneSignal ID if needed
                println("AppInitialization: 4/5 - Checking OneSignal ID...")
                oneSignalService.checkAndUpdateOneSignalId()

                println("AppInitialization: 5/5 - All services initialized successfully! ✅")

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
}