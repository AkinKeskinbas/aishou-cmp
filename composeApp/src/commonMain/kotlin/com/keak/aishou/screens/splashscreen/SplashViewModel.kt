package com.keak.aishou.screens.splashscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keak.aishou.data.UserSessionManager
import com.keak.aishou.notifications.OneSignalService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            try {
                // Initialize OneSignal (but don't register yet - need token first)
                withContext(Dispatchers.Main) {
                    oneSignalService.initialize()
                }

                // Perform background operations on IO dispatcher
                val isFirstTime = withContext(Dispatchers.Default) {
                    userSessionManager.isUserFirstTime()
                }

                withContext(Dispatchers.Default) {
                    userSessionManager.handleAppStart()
                }

                // Add a small delay for splash screen effect
                delay(2000)

                // Update navigation state on Main thread
                withContext(Dispatchers.Main) {
                    if (isFirstTime) {
                        // Navigate to onboarding for first-time users
                        _navigationEvent.value = SplashNavigationEvent.NavigateToOnboarding
                    } else {
                        // Navigate to home for returning users
                        _navigationEvent.value = SplashNavigationEvent.NavigateToHome
                    }
                }
            } catch (e: Exception) {
                println("SplashViewModel: Error during initialization: ${e.message}")
                // Fallback to onboarding on error
                withContext(Dispatchers.Main) {
                    _navigationEvent.value = SplashNavigationEvent.NavigateToOnboarding
                }
            }
        }
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }
}