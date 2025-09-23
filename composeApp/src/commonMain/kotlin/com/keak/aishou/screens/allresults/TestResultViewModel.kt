package com.keak.aishou.screens.allresults

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keak.aishou.data.api.TestResultResponse
import com.keak.aishou.data.api.ResultType
import com.keak.aishou.data.api.InviteCreateRequest
import com.keak.aishou.network.AishouApiService
import com.keak.aishou.network.ApiResult
import com.keak.aishou.purchase.PremiumChecker
import com.keak.aishou.data.UserSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TestResultViewModel(
    private val apiService: AishouApiService,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _testResult = MutableStateFlow<TestResultResponse?>(null)
    val testResult: StateFlow<TestResultResponse?> = _testResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isReprocessing = MutableStateFlow(false)
    val isReprocessing: StateFlow<Boolean> = _isReprocessing.asStateFlow()

    private val _isSendingInvite = MutableStateFlow(false)
    val isSendingInvite: StateFlow<Boolean> = _isSendingInvite.asStateFlow()

    private val _inviteSuccess = MutableStateFlow<String?>(null)
    val inviteSuccess: StateFlow<String?> = _inviteSuccess.asStateFlow()

    private val _showSendToFriendBottomSheet = MutableStateFlow(false)
    val showSendToFriendBottomSheet: StateFlow<Boolean> = _showSendToFriendBottomSheet.asStateFlow()

    private val _inviteLink = MutableStateFlow<String?>(null)
    val inviteLink: StateFlow<String?> = _inviteLink.asStateFlow()

    fun loadTestResults(testId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = apiService.getTestResults(testId)
                when (result) {
                    is ApiResult.Success -> {
                        val resultData = result.data.data
                        _testResult.value = resultData
                        _error.value = null
                        println("TestResultViewModel: Test results loaded successfully")
                        println("TestResultViewModel: Result type: ${resultData?.resultType}")
                        println("TestResultViewModel: Solo result present: ${resultData?.soloResult != null}")
                        println("TestResultViewModel: Compatibility results count: ${resultData?.compatibilityResults?.size}")
                    }
                    is ApiResult.Error -> {
                        val errorMessage = result.message ?: "Failed to load test results"
                        _error.value = errorMessage
                        println("TestResultViewModel: Error loading test results: $errorMessage")
                    }
                    is ApiResult.Exception -> {
                        val errorMessage = result.exception.message ?: "Exception loading test results"
                        _error.value = errorMessage
                        println("TestResultViewModel: Exception loading test results: $errorMessage")
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unexpected error loading test results"
                _error.value = errorMessage
                println("TestResultViewModel: Unexpected error: $errorMessage")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun retryLoad(testId: String) {
        loadTestResults(testId)
    }

    fun loadCompatibilityResult(testId: String) {
        // All tests now use the same endpoint with testId
        println("TestResultViewModel: Loading test result for testId: $testId")
        loadTestResults(testId)
    }

    fun getResultType(): ResultType {
        return _testResult.value?.resultType?.let { ResultType.fromString(it) } ?: ResultType.NONE
    }

    fun hasSoloResult(): Boolean {
        return _testResult.value?.soloResult != null
    }

    fun hasCompatibilityResults(): Boolean {
        return _testResult.value?.compatibilityResults?.isNotEmpty() == true
    }

    fun shouldShowSendToFriendButton(): Boolean {
        val resultType = getResultType()
        return resultType == ResultType.SOLO || resultType == ResultType.BOTH
    }

    fun isPremiumUser(): Boolean {
        return PremiumChecker.isPremium
    }

    fun shouldShowPremiumPrompt(): Boolean {
        // Premium değilse ve MBTI insights yoksa premium prompt göster
        return !isPremiumUser() && _testResult.value?.soloResult?.personalizedInsights.isNullOrBlank()
    }

    fun shouldShowReprocessButton(): Boolean {
        // Premium kullanıcıysa ama MBTI insights yoksa reprocess butonu göster
        return isPremiumUser() && _testResult.value?.soloResult?.personalizedInsights.isNullOrBlank()
    }

    fun reprocessTestResults(testId: String) {
        viewModelScope.launch {
            _isReprocessing.value = true
            _error.value = null

            try {
                val result = apiService.reprocessTestResults(testId)
                when (result) {
                    is ApiResult.Success -> {
                        val resultData = result.data.data
                        _testResult.value = resultData
                        _error.value = null
                        println("TestResultViewModel: Test reprocessing successful")
                        println("TestResultViewModel: New insights available: ${!resultData?.soloResult?.personalizedInsights.isNullOrBlank()}")
                    }
                    is ApiResult.Error -> {
                        val errorMessage = result.message ?: "Failed to reprocess test results"
                        _error.value = errorMessage
                        println("TestResultViewModel: Error reprocessing test results: $errorMessage")
                    }
                    is ApiResult.Exception -> {
                        val errorMessage = result.exception.message ?: "Exception reprocessing test results"
                        _error.value = errorMessage
                        println("TestResultViewModel: Exception reprocessing test results: $errorMessage")
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unexpected error reprocessing test results"
                _error.value = errorMessage
                println("TestResultViewModel: Unexpected error: $errorMessage")
            } finally {
                _isReprocessing.value = false
            }
        }
    }

    fun openSendToFriendBottomSheet(testId: String) {
        _showSendToFriendBottomSheet.value = true
        sendToFriend(testId)
    }

    fun closeSendToFriendBottomSheet() {
        _showSendToFriendBottomSheet.value = false
        _inviteLink.value = null
        _inviteSuccess.value = null
    }

    fun sendToFriend(testId: String) {
        viewModelScope.launch {
            _isSendingInvite.value = true
            _error.value = null
            _inviteSuccess.value = null
            _inviteLink.value = null

            try {
                // Get test version from test result response
                val testVersion = _testResult.value?.let { result ->
                    // Extract version from test data if available, default to 1
                    1 // For now, using version 1 as default - you may need to get this from the test data
                } ?: 1

                val inviteRequest = InviteCreateRequest(
                    friendId = null, // null means public invite
                    testId = testId,
                    version = testVersion
                )

                val result = apiService.createInvite(inviteRequest)
                when (result) {
                    is ApiResult.Success -> {
                        val inviteId = result.data.data?.inviteId
                        _inviteSuccess.value = inviteId

                        // Generate invite link from invite ID
                        val inviteLink = generateInviteLink(inviteId)
                        _inviteLink.value = inviteLink

                        _error.value = null
                        println("TestResultViewModel: Invite created successfully with ID: $inviteId")
                        println("TestResultViewModel: Invite link: $inviteLink")
                    }
                    is ApiResult.Error -> {
                        val errorMessage = result.message ?: "Failed to create invite"
                        _error.value = errorMessage
                        println("TestResultViewModel: Error creating invite: $errorMessage")
                    }
                    is ApiResult.Exception -> {
                        val errorMessage = result.exception.message ?: "Exception creating invite"
                        _error.value = errorMessage
                        println("TestResultViewModel: Exception creating invite: $errorMessage")
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unexpected error creating invite"
                _error.value = errorMessage
                println("TestResultViewModel: Unexpected error: $errorMessage")
            } finally {
                _isSendingInvite.value = false
            }
        }
    }

    private fun generateInviteLink(inviteId: String?, testId: String? = null, senderId: String? = null, testTitle: String? = null): String? {
        return if (inviteId != null) {
            val actualSenderId = senderId ?: getCurrentUserId() ?: "unknown"
            val actualTestId = testId ?: _testResult.value?.testId ?: "unknown"
            val actualTestTitle = testTitle ?: "Test" // We'll get this from tests API when needed

            // Create deep link URL - replace with actual production domain
            "https://aishou.app/invite/$inviteId?senderId=$actualSenderId&testId=$actualTestId&testTitle=${actualTestTitle.replace(" ", "%20").replace("&", "%26")}"
        } else {
            null
        }
    }

    private fun getCurrentUserId(): String? {
        return try {
            // Get current user ID from UserSessionManager
            runBlocking { userSessionManager.userId.first() }
        } catch (e: Exception) {
            println("TestResultViewModel: Error getting current user ID: ${e.message}")
            null
        }
    }

    fun clearInviteSuccess() {
        _inviteSuccess.value = null
    }

    // Create invite for specific friend
    fun createInviteForFriend(testId: String, friendId: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                println("TestResultViewModel: Creating invite for friend $friendId")

                // Get test version from test result response
                val testVersion = _testResult.value?.let { result ->
                    // Extract version from test data if available, default to 1
                    1 // For now, using version 1 as default
                } ?: 1

                val inviteRequest = InviteCreateRequest(
                    friendId = friendId, // Specific friend ID
                    testId = testId,
                    version = testVersion
                )

                val result = apiService.createInvite(inviteRequest)
                when (result) {
                    is ApiResult.Success -> {
                        val inviteId = result.data.data?.inviteId
                        if (inviteId != null) {
                            // Use provided test title or default
                            val testTitle = "Test" // We'll load this properly from tests API later
                            val inviteLink = generateInviteLink(
                                inviteId = inviteId,
                                testId = testId,
                                senderId = getCurrentUserId(),
                                testTitle = testTitle
                            )
                            if (inviteLink != null) {
                                println("TestResultViewModel: Created friend invite successfully: $inviteLink")
                                onSuccess(inviteLink)
                            } else {
                                onError("Failed to generate invite link")
                            }
                        } else {
                            onError("No invite ID received")
                        }
                    }
                    is ApiResult.Error -> {
                        val errorMessage = result.message ?: "Failed to create friend invite"
                        println("TestResultViewModel: Error creating friend invite: $errorMessage")
                        onError(errorMessage)
                    }
                    is ApiResult.Exception -> {
                        val errorMessage = result.exception.message ?: "Exception creating friend invite"
                        println("TestResultViewModel: Exception creating friend invite: $errorMessage")
                        onError(errorMessage)
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unexpected error creating friend invite"
                println("TestResultViewModel: Unexpected error: $errorMessage")
                onError(errorMessage)
            }
        }
    }
}