package com.keak.aishou.screens.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keak.aishou.data.UserSessionManager
import com.keak.aishou.data.UserState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val userSessionManager: UserSessionManager) : ViewModel() {

    val userState: StateFlow<UserState> = combine(
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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserState()
    )
}