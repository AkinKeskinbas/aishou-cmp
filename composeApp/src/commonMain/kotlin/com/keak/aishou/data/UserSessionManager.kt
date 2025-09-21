package com.keak.aishou.data

import com.keak.aishou.purchase.RevenueCatUserHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class UserSessionManager(
    private val dataStoreManager: DataStoreManager,
    private val scope: CoroutineScope
) {

    val isFirstTimeUser: Flow<Boolean> = dataStoreManager.isFirstTimeUser
    val userId: Flow<String?> = dataStoreManager.userId
    val firstLaunchTimestamp: Flow<Long?> = dataStoreManager.firstLaunchTimestamp
    val appLaunchCount: Flow<Long> = dataStoreManager.appLaunchCount

    @OptIn(ExperimentalTime::class)
    suspend fun handleAppStart() {
        val isFirstTime = dataStoreManager.isFirstTimeUser.firstOrNull() ?: true

        if (isFirstTime) {
            val revenueCatUserId = getRevenueCatUserId()
            val currentTimestamp = Clock.System.now().epochSeconds

            if (revenueCatUserId != null) {
                dataStoreManager.initializeFirstTimeUser(revenueCatUserId, currentTimestamp)
                println("UserSessionManager: Initialized with RevenueCat ID: $revenueCatUserId")
            } else {
                println("UserSessionManager: ❌ Failed to get RevenueCat user ID")
                // Don't initialize user session if we can't get RevenueCat ID
                // This prevents creating invalid user sessions
            }
        } else {
            dataStoreManager.incrementLaunchCount()
        }
    }


    suspend fun isUserFirstTime(): Boolean {
        return dataStoreManager.isFirstTimeUser.firstOrNull() ?: true
    }

    suspend fun getUserId(): String? {
        return dataStoreManager.userId.firstOrNull()
    }

    suspend fun getLaunchCount(): Long {
        return dataStoreManager.appLaunchCount.firstOrNull() ?: 0L
    }

    suspend fun getFirstLaunchTimestamp(): Long? {
        return dataStoreManager.firstLaunchTimestamp.firstOrNull()
    }

    /**
     * Get RevenueCat user ID
     */
    private suspend fun getRevenueCatUserId(): String? {
        return try {
            val userId = RevenueCatUserHelper.getRevenueCatUserId()
            // Extra safety check for iOS
            val safeUserId = userId?.takeIf { it.isNotBlank() && it != "null" }

            if (safeUserId != null) {
                println("UserSessionManager: Retrieved RevenueCat user ID: $safeUserId")
                safeUserId
            } else {
                println("UserSessionManager: RevenueCat user ID is null or invalid")
                null
            }
        } catch (e: Exception) {
            println("UserSessionManager: Error getting RevenueCat user ID: ${e.message}")
            null
        }
    }

    /**
     * Ensure we have a valid RevenueCat user ID
     */
    suspend fun ensureRevenueCatUserId(): String? {
        val storedUserId = getUserId()

        if (storedUserId != null) {
            return storedUserId
        }

        // Try to get RevenueCat ID if we don't have one stored
        val revenueCatUserId = getRevenueCatUserId()
        if (revenueCatUserId != null) {
            dataStoreManager.setUserId(revenueCatUserId)
            println("UserSessionManager: Stored RevenueCat user ID: $revenueCatUserId")
            return revenueCatUserId
        }

        return null
    }

    companion object {
        private var INSTANCE: UserSessionManager? = null

        fun getInstance(dataStoreManager: DataStoreManager, scope: CoroutineScope): UserSessionManager {
            return INSTANCE ?: UserSessionManager(dataStoreManager, scope).also { INSTANCE = it }
        }
    }
}