package com.keak.aishou.screens.reauth

import com.keak.aishou.data.UserSessionManager
import com.keak.aishou.data.UserRegistrationService

/**
 * Test helper for ReAuth scenarios
 * REMOVE THIS FILE IN PRODUCTION!
 */
object ReAuthTestHelper {

    /**
     * Test Scenario 1: Simulate unauthorized user (401 response)
     */
    suspend fun simulateUnauthorizedUser(userSessionManager: UserSessionManager) {
        println("🧪 TEST: Simulating unauthorized user...")
        userSessionManager.handleUnauthorizedUser()
        println("🧪 TEST: User marked as unauthorized, tokens cleared")
    }

    /**
     * Test Scenario 2: Simulate too many retry attempts
     */
    suspend fun simulateTooManyRetries(userSessionManager: UserSessionManager) {
        println("🧪 TEST: Simulating too many retries...")

        // Mark 3 failed attempts
        repeat(3) {
            userSessionManager.markAuthAttempt()
            println("🧪 TEST: Marked retry attempt ${it + 1}")
        }

        println("🧪 TEST: Max retry attempts reached")
    }

    /**
     * Test Scenario 3: Simulate recent retry attempt (throttling)
     */
    suspend fun simulateRecentRetry(userSessionManager: UserSessionManager) {
        println("🧪 TEST: Simulating recent retry attempt...")
        userSessionManager.markAuthAttempt()
        println("🧪 TEST: Recent attempt marked (should be throttled)")
    }

    /**
     * Test Scenario 4: Reset auth state for fresh testing
     */
    suspend fun resetAuthState(userSessionManager: UserSessionManager) {
        println("🧪 TEST: Resetting auth state...")
        userSessionManager.resetAuthAttempts()
        println("🧪 TEST: Auth state reset")
    }

    /**
     * Test Scenario 5: Check current auth status
     */
    suspend fun checkAuthStatus(userSessionManager: UserSessionManager): String {
        val hasAuth = userSessionManager.hasValidAuthentication()
        val shouldRetry = userSessionManager.shouldAttemptReauth()
        val retryCount = userSessionManager.getAuthRetryCount()

        return """
            🧪 AUTH STATUS:
            - Has Valid Auth: $hasAuth
            - Should Retry: $shouldRetry
            - Retry Count: $retryCount
            - Is First Time: ${userSessionManager.isUserFirstTime()}
        """.trimIndent()
    }
}