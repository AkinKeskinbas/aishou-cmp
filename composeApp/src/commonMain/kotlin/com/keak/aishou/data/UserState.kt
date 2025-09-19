package com.keak.aishou.data

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class UserState(
    val isFirstTimeUser: Boolean = true,
    val userId: String? = null,
    val firstLaunchTimestamp: Long? = null,
    val appLaunchCount: Long = 0L
) {
    val isReturningUser: Boolean
        get() = !isFirstTimeUser

    @OptIn(ExperimentalTime::class)
    val daysSinceFirstLaunch: Long?
        get() = firstLaunchTimestamp?.let { timestampSeconds ->
            val currentSeconds = Clock.System.now().epochSeconds
            val diffSeconds = currentSeconds - timestampSeconds
            diffSeconds / (60 * 60 * 24) // Convert seconds to days
        }
}