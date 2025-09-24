package com.keak.aishou.screens.reauth

import aishou.composeapp.generated.resources.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keak.aishou.data.UserRegistrationService
import com.keak.aishou.data.UserSessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

enum class ReAuthState {
    Checking,
    Reregistering,
    Error,
    Success
}

sealed class ReAuthNavigationEvent {
    object NavigateToHome : ReAuthNavigationEvent()
    object NavigateToSupport : ReAuthNavigationEvent()
}

data class ReAuthUiState(
    val state: ReAuthState = ReAuthState.Checking,
    val progress: Float = 0f,
    val errorMessage: String = "",
    val canRetry: Boolean = false,
    val navigationEvent: ReAuthNavigationEvent? = null
)

class ReAuthViewModel(
    private val userSessionManager: UserSessionManager,
    private val userRegistrationService: UserRegistrationService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReAuthUiState())
    val uiState: StateFlow<ReAuthUiState> = _uiState.asStateFlow()

    init {
        startAuthCheck()
    }

    private fun startAuthCheck() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(state = ReAuthState.Checking)

                // Simulate checking delay for better UX
                delay(1000)

                // Check if we should attempt reauth
                if (!userSessionManager.shouldAttemptReauth()) {
                    val retryCount = userSessionManager.getAuthRetryCount()
                    _uiState.value = _uiState.value.copy(
                        state = ReAuthState.Error,
                        errorMessage = if (retryCount >= 3) {
                            getString(Res.string.reauth_too_many_attempts)
                        } else {
                            getString(Res.string.reauth_recent_attempt)
                        },
                        canRetry = retryCount < 3
                    )
                    return@launch
                }

                // Mark auth attempt
                userSessionManager.markAuthAttempt()

                // Start reregistration
                _uiState.value = _uiState.value.copy(
                    state = ReAuthState.Reregistering,
                    progress = 0.1f
                )

                // Simulate progress updates
                for (progress in listOf(0.2f, 0.4f, 0.6f, 0.8f)) {
                    delay(500)
                    _uiState.value = _uiState.value.copy(progress = progress)
                }

                // Attempt reregistration
                userRegistrationService.forceReregister()

                // Final progress
                _uiState.value = _uiState.value.copy(progress = 1.0f)
                delay(500)

                // Check if registration was successful
                if (userSessionManager.hasValidAuthentication()) {
                    userSessionManager.resetAuthAttempts()
                    _uiState.value = _uiState.value.copy(state = ReAuthState.Success)

                    delay(1500) // Show success state briefly
                    _uiState.value = _uiState.value.copy(
                        navigationEvent = ReAuthNavigationEvent.NavigateToHome
                    )
                } else {
                    throw Exception(getString(Res.string.reauth_unknown_error))
                }

            } catch (e: Exception) {
                val retryCount = userSessionManager.getAuthRetryCount()
                _uiState.value = _uiState.value.copy(
                    state = ReAuthState.Error,
                    errorMessage = e.message ?: getString(Res.string.reauth_unknown_error),
                    canRetry = retryCount < 3
                )
            }
        }
    }

    fun retryAuth() {
        viewModelScope.launch {
            if (userSessionManager.shouldAttemptReauth()) {
                startAuthCheck()
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = getString(Res.string.reauth_recent_attempt),
                    canRetry = false
                )
            }
        }
    }

    fun navigateToSupport() {
        _uiState.value = _uiState.value.copy(
            navigationEvent = ReAuthNavigationEvent.NavigateToSupport
        )
    }

    fun onNavigationHandled() {
        _uiState.value = _uiState.value.copy(navigationEvent = null)
    }
}