package com.keak.aishou.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keak.aishou.data.UserSessionManager
import com.keak.aishou.data.api.UserProfileResponse
import com.keak.aishou.network.AishouApiService
import com.keak.aishou.network.ApiResult
import com.keak.aishou.purchase.PremiumPresenter
import com.keak.aishou.purchase.PremiumState
import com.keak.aishou.util.FormatUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val apiService: AishouApiService,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfileResponse?>(null)
    val userProfile: StateFlow<UserProfileResponse?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    var appLaunchedTime by mutableStateOf("")

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                appLaunchedTime =
                    FormatUtils.formatLaunchCount(userSessionManager.appLaunchCount.first())
                when (val result = apiService.getUserProfile()) {
                    is ApiResult.Success -> {
                        val profileData = result.data.data
                        _userProfile.value = profileData
                        _error.value = null
                        println("ProfileViewModel: Profile loaded successfully")
                        println("ProfileViewModel: UserName:${profileData?.displayName}")
                    }

                    is ApiResult.Error -> {
                        val errorMessage = result.message ?: "Failed to load profile"
                        _error.value = errorMessage
                        println("ProfileViewModel: Error loading profile: $errorMessage")
                    }

                    is ApiResult.Exception -> {
                        val errorMessage = result.exception.message ?: "Exception loading profile"
                        _error.value = errorMessage
                        println("ProfileViewModel: Exception loading profile: $errorMessage")
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unexpected error loading profile"
                _error.value = errorMessage
                println("ProfileViewModel: Unexpected error: $errorMessage")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun retryLoadProfile() {
        loadProfile()
    }
}