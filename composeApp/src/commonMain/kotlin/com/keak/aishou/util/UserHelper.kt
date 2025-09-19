package com.keak.aishou.util

import com.keak.aishou.data.UserSessionManager
import com.keak.aishou.data.UserState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object UserHelper : KoinComponent {

    private val userSessionManager: UserSessionManager by inject()

    fun getUserState(): Flow<UserState> {
        return combine(
            userSessionManager.isFirstTimeUser,
            userSessionManager.userId,
            userSessionManager.firstLaunchTimestamp,
            userSessionManager.appLaunchCount
        ) { isFirstTime, userId, timestamp, count ->
            UserState(
                isFirstTimeUser = isFirstTime,
                userId = userId,
                firstLaunchTimestamp = timestamp,
                appLaunchCount = count
            )
        }
    }

    suspend fun isFirstTimeUser(): Boolean {
        return userSessionManager.isUserFirstTime()
    }

    suspend fun getUserId(): String? {
        return userSessionManager.getUserId()
    }

    suspend fun getLaunchCount(): Long {
        return userSessionManager.getLaunchCount()
    }

    suspend fun getFirstLaunchTimestamp(): Long? {
        return userSessionManager.getFirstLaunchTimestamp()
    }

    @OptIn(ExperimentalTime::class)
    suspend fun getDaysSinceFirstLaunch(): Long? {
        return getFirstLaunchTimestamp()?.let { timestampSeconds ->
            val currentSeconds = Clock.System.now().epochSeconds
            val diffSeconds = currentSeconds - timestampSeconds
            diffSeconds / (60 * 60 * 24) // Convert seconds to days
        }
    }
}