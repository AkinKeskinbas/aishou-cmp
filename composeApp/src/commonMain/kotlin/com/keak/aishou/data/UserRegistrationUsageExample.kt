package com.keak.aishou.data

/**
 * Example usage of the User Registration System
 *
 * This file shows how to initialize and use the user registration system
 * in your app startup.
 */

/*
// In your App/Application class (Android) or App struct (iOS)

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin with all modules
        startKoin {
            androidContext(this@MyApplication)
            modules(dataModules, domainModule, viewModelModule, androidModule)
        }

        // Initialize app services - this will handle user registration automatically
        val appInitService = get<AppInitializationService>()
        appInitService.initialize()
    }
}

// Or in iOS SwiftUI App:
struct iOSApp: App {
    init() {
        // RevenueCat and OneSignal initialization...

        // Initialize Koin
        KoinKt.doInitKoin()

        // Initialize app services
        let appInitService = KoinHelper.getAppInitializationService()
        appInitService.initialize()
    }
}

// What happens when the app starts:
// 1. LanguageManager detects system language and stores it
// 2. OneSignal is initialized
// 3. UserRegistrationService checks if this is first app open
// 4. If first time, it automatically sends a POST request to /v1/auth/register with:
//    {
//        "revenueCatId": "generated-user-id",
//        "displayName": null,
//        "photoUrl": null,
//        "lang": "en", // or detected language
//        "platform": "android", // or "ios"
//        "isAnonymous": true
//    }

// Manual registration (if needed):
class MyViewModel(
    private val userRegistrationService: UserRegistrationService
) : ViewModel() {

    suspend fun checkRegistrationStatus() {
        val isRegistered = userRegistrationService.isUserRegistered()
        println("User is registered: $isRegistered")
    }

    suspend fun forceReregister() {
        userRegistrationService.forceReregister()
    }
}

// Debugging registration:
// Check console logs for:
// - "UserRegistration: First time user or no user ID - registering user..."
// - "UserRegistration: ✅ User registered successfully!"
// - "UserRegistration: ❌ Registration failed: [error message]"

// Backend endpoint should expect:
// POST /v1/auth/register
// Content-Type: application/json
// {
//   "revenueCatId": "string",      // RevenueCat user ID (required)
//   "displayName": "string",       // null for anonymous users
//   "photoUrl": "string",          // null for anonymous users
//   "lang": "string",              // "en", "tr", "es", etc.
//   "platform": "string",          // "android" or "ios"
//   "isAnonymous": boolean         // true for first-time anonymous users
// }
//
// Response should be:
// {
//   "success": true,
//   "data": {
//     "token": "jwt-token-here"
//   }
// }
*/