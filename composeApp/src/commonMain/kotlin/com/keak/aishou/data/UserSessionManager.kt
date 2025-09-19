package com.keak.aishou.data

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
            val newUserId = generateUserId()
            val currentTimestamp = Clock.System.now().epochSeconds
            dataStoreManager.initializeFirstTimeUser(newUserId, currentTimestamp)
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

    private fun generateUserId(): String {
        val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
        val randomString = (1..16)
            .map { chars.random() }
            .joinToString("")
        return "user_$randomString"
    }

    companion object {
        private var INSTANCE: UserSessionManager? = null

        fun getInstance(dataStoreManager: DataStoreManager, scope: CoroutineScope): UserSessionManager {
            return INSTANCE ?: UserSessionManager(dataStoreManager, scope).also { INSTANCE = it }
        }
    }
}