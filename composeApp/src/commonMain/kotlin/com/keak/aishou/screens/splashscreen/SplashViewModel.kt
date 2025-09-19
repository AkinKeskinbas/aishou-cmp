package com.keak.aishou.screens.splashscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keak.aishou.data.UserSessionManager
import com.keak.aishou.notifications.OneSignalService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SplashNavigationEvent {
    object NavigateToOnboarding : SplashNavigationEvent()
    object NavigateToHome : SplashNavigationEvent()
}

class SplashViewModel(
    private val userSessionManager: UserSessionManager,
    private val oneSignalService: OneSignalService
) : ViewModel() {

    private val _navigationEvent = MutableStateFlow<SplashNavigationEvent?>(null)
    val navigationEvent: StateFlow<SplashNavigationEvent?> = _navigationEvent.asStateFlow()

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            // Initialize OneSignal
            oneSignalService.initialize()

            // Check if this is the user's first time BEFORE handling app start
            val isFirstTime = userSessionManager.isUserFirstTime()

            // Handle app start logic (increment launch count, etc.)
            userSessionManager.handleAppStart()

            // Add a small delay for splash screen effect
            delay(2000)

            if (isFirstTime) {
                // Navigate to onboarding for first-time users
                _navigationEvent.value = SplashNavigationEvent.NavigateToOnboarding
            } else {
                // Navigate to home for returning users
                _navigationEvent.value = SplashNavigationEvent.NavigateToHome
            }
        }
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }
}