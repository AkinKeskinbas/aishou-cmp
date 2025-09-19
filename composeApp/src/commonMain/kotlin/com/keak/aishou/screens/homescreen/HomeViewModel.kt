package com.keak.aishou.screens.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keak.aishou.data.UserSessionManager
import com.keak.aishou.data.UserState
import com.keak.aishou.notifications.OneSignalService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userSessionManager: UserSessionManager,
    private val oneSignalService: OneSignalService
) : ViewModel() {

    private val _notificationPermissionRequested = MutableStateFlow(false)
    val notificationPermissionRequested: StateFlow<Boolean> = _notificationPermissionRequested.asStateFlow()

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

    init {
        // Request notification permission after a short delay when home screen loads
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000) // Give user time to see the home screen
            requestNotificationPermission()
        }
    }

    fun requestNotificationPermission() {
        if (_notificationPermissionRequested.value) return

        viewModelScope.launch {
            try {
                val granted = oneSignalService.requestPermissionAndRegister()
                _notificationPermissionRequested.value = true

                if (granted) {
                    println("Notification permission granted and OneSignal registered")
                } else {
                    println("Notification permission denied")
                }
            } catch (e: Exception) {
                println("Error requesting notification permission: ${e.message}")
                _notificationPermissionRequested.value = true
            }
        }
    }
}