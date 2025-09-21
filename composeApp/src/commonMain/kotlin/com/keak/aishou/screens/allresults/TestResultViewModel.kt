package com.keak.aishou.screens.allresults

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keak.aishou.data.api.TestResultResponse
import com.keak.aishou.data.api.ResultType
import com.keak.aishou.data.api.InviteCreateRequest
import com.keak.aishou.network.AishouApiService
import com.keak.aishou.network.ApiResult
import com.keak.aishou.purchase.PremiumChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TestResultViewModel(
    private val apiService: AishouApiService
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

    private fun generateInviteLink(inviteId: String?): String? {
        return if (inviteId != null) {
            // TODO: Replace with actual deep link domain
            "https://aishou.app/invite/$inviteId"
        } else {
            null
        }
    }

    fun clearInviteSuccess() {
        _inviteSuccess.value = null
    }
}