package com.keak.aishou.screens.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keak.aishou.data.UserSessionManager
import com.keak.aishou.data.UserState
import com.keak.aishou.data.api.UserProfileResponse
import com.keak.aishou.data.api.SolvedTest
import com.keak.aishou.network.AishouApiService
import com.keak.aishou.network.ApiResult
import com.keak.aishou.notifications.OneSignalService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.keak.aishou.response.isSuccess

class HomeViewModel(
    private val userSessionManager: UserSessionManager,
    private val oneSignalService: OneSignalService,
    private val apiService: AishouApiService
) : ViewModel() {


    private val _userProfile = MutableStateFlow<UserProfileResponse?>(null)
    val userProfile: StateFlow<UserProfileResponse?> = _userProfile.asStateFlow()

    private val _isLoadingProfile = MutableStateFlow(false)
    val isLoadingProfile: StateFlow<Boolean> = _isLoadingProfile.asStateFlow()

    private val _profileError = MutableStateFlow<String?>(null)
    val profileError: StateFlow<String?> = _profileError.asStateFlow()

    private val _unreadNotificationCount = MutableStateFlow(0L)
    val unreadNotificationCount: StateFlow<Long> = _unreadNotificationCount.asStateFlow()

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
        // Load user profile when ViewModel is initialized
        loadUserProfile()

        // Load unread notification count when ViewModel is initialized
        loadUnreadNotificationCount()
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
                        println("HomeViewModel: User profile loaded successfully")
                        println("HomeViewModel: User Name:${profileData?.displayName}")
                        println("HomeViewModel: MBTI: ${profileData?.mbti}, Zodiac: ${profileData?.zodiac}")
                        println("HomeViewModel: Solo quizzes: ${profileData?.soloQuizzes?.size}")
                        println("HomeViewModel: Match quizzes: ${profileData?.matchQuizzes?.size}")
                        println("HomeViewModel: Solved tests: ${profileData?.solvedTests?.size}")
                        profileData?.solvedTests?.forEachIndexed { index, test ->
                            println("HomeViewModel: Test $index: id=${test.id}, type=${test.type}, score=${test.score}, title=${test.title}")
                        }
                    }
                    is ApiResult.Error -> {
                        val errorMessage = result.message ?: "Failed to load profile"
                        _profileError.value = errorMessage
                        println("HomeViewModel: Error loading profile: $errorMessage")
                    }
                    is ApiResult.Exception -> {
                        val errorMessage = result.exception.message ?: "Exception loading profile"
                        _profileError.value = errorMessage
                        println("HomeViewModel: Exception loading profile: $errorMessage")
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unexpected error loading profile"
                _profileError.value = errorMessage
                println("HomeViewModel: Unexpected error: $errorMessage")
            } finally {
                _isLoadingProfile.value = false
            }
        }
    }

    fun retryLoadProfile() {
        loadUserProfile()
    }

    fun getDisplayableTests(): List<SolvedTest> {
        return _userProfile.value?.solvedTests?.take(3) ?: emptyList()
    }

    fun loadUnreadNotificationCount() {
        viewModelScope.launch {
            try {
                when (val result = apiService.getUnreadRequestsCount()) {
                    is ApiResult.Success -> {
                        if (result.data.isSuccess() && result.data.data != null) {
                            _unreadNotificationCount.value = result.data.data.count
                            println("HomeViewModel: Unread notification count loaded: ${result.data.data.count}")
                        } else {
                            _unreadNotificationCount.value = 0
                        }
                    }
                    is ApiResult.Error -> {
                        if (result.code == 500) {
                            println("HomeViewModel: Server error (500) loading unread count - backend issue")
                        } else {
                            println("HomeViewModel: Error loading unread count: ${result.message}")
                        }
                        _unreadNotificationCount.value = 0
                    }
                    is ApiResult.Exception -> {
                        println("HomeViewModel: Exception loading unread count: ${result.exception.message}")
                        _unreadNotificationCount.value = 0
                    }
                }
            } catch (e: Exception) {
                println("HomeViewModel: Unexpected error loading unread count: ${e.message}")
                _unreadNotificationCount.value = 0
            }
        }
    }

    fun requestNotificationPermission() {
        viewModelScope.launch {
            try {
                println("HomeViewModel: Requesting notification permission...")
                val permissionGranted = oneSignalService.requestNotificationPermission()
                println("HomeViewModel: Permission granted: $permissionGranted")
            } catch (e: Exception) {
                println("HomeViewModel: Error requesting notification permission: ${e.message}")
            }
        }
    }
}