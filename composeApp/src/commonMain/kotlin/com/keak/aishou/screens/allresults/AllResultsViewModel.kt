package com.keak.aishou.screens.allresults

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keak.aishou.data.api.UserProfileResponse
import com.keak.aishou.data.api.SolvedTest
import com.keak.aishou.network.AishouApiService
import com.keak.aishou.network.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AllResultsViewModel(
    private val apiService: AishouApiService
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfileResponse?>(null)
    val userProfile: StateFlow<UserProfileResponse?> = _userProfile.asStateFlow()

    private val _isLoadingProfile = MutableStateFlow(false)
    val isLoadingProfile: StateFlow<Boolean> = _isLoadingProfile.asStateFlow()

    private val _profileError = MutableStateFlow<String?>(null)
    val profileError: StateFlow<String?> = _profileError.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoadingProfile.value = true
            _profileError.value = null

            try {
                val result = apiService.getUserProfile()
                when (result) {
                    is ApiResult.Success -> {
                        val profileData = result.data.data
                        _userProfile.value = profileData
                        _profileError.value = null
                        println("AllResultsViewModel: User profile loaded successfully")
                        println("AllResultsViewModel: Total tests available: ${profileData?.solvedTests?.size}")
                    }
                    is ApiResult.Error -> {
                        val errorMessage = result.message ?: "Failed to load profile"
                        _profileError.value = errorMessage
                        println("AllResultsViewModel: Error loading profile: $errorMessage")
                    }
                    is ApiResult.Exception -> {
                        val errorMessage = result.exception.message ?: "Exception loading profile"
                        _profileError.value = errorMessage
                        println("AllResultsViewModel: Exception loading profile: $errorMessage")
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unexpected error loading profile"
                _profileError.value = errorMessage
                println("AllResultsViewModel: Unexpected error: $errorMessage")
            } finally {
                _isLoadingProfile.value = false
            }
        }
    }

    fun retryLoadProfile() {
        loadUserProfile()
    }

    fun getAllTests(): List<SolvedTest> {
        return _userProfile.value?.solvedTests ?: emptyList()
    }
}