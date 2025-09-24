package com.keak.aishou.screens.invite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keak.aishou.network.AishouApiService
import com.keak.aishou.network.ApiResult
import com.keak.aishou.response.isSuccess
import com.keak.aishou.purchase.PremiumChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InviteViewModel(
    private val apiService: AishouApiService
) : ViewModel() {

    private val _senderInfo = MutableStateFlow<SenderInfo?>(null)
    val senderInfo: StateFlow<SenderInfo?> = _senderInfo.asStateFlow()

    private val _testInfo = MutableStateFlow<TestInfo?>(null)
    val testInfo: StateFlow<TestInfo?> = _testInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _userPremiumStatus = MutableStateFlow<Boolean?>(null)
    val userPremiumStatus: StateFlow<Boolean?> = _userPremiumStatus.asStateFlow()

    private val _isAcceptingInvite = MutableStateFlow(false)
    val isAcceptingInvite: StateFlow<Boolean> = _isAcceptingInvite.asStateFlow()

    private val _inviteAccepted = MutableStateFlow(false)
    val inviteAccepted: StateFlow<Boolean> = _inviteAccepted.asStateFlow()

    init {
        // Check user's premium status
        checkPremiumStatus()
    }

    fun loadInviteData(senderId: String, testId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Load sender info and test info in parallel
                val senderJob = launch { loadSenderInfo(senderId) }
                val testJob = launch { loadTestInfo(testId) }

                // Wait for both to complete
                senderJob.join()
                testJob.join()

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load invitation data"
                println("InviteViewModel: Error loading invite data: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadSenderInfo(senderId: String) {
        try {
            // For now, we'll create mock sender info since we don't have a specific endpoint
            // In real implementation, you might have a getUserProfile endpoint
            println("InviteViewModel: Loading sender info for $senderId")

            // Mock data - replace with actual API call
            _senderInfo.value = SenderInfo(
                userId = senderId,
                displayName = "Friend User", // This should come from API
                mbtiType = "ENFP", // This should come from API
                zodiacSign = "Gemini" // This should come from API
            )
        } catch (e: Exception) {
            println("InviteViewModel: Error loading sender info: ${e.message}")
            // Set basic sender info even if API fails
            _senderInfo.value = SenderInfo(
                userId = senderId,
                displayName = null,
                mbtiType = null,
                zodiacSign = null
            )
        }
    }

    private suspend fun loadTestInfo(testId: String) {
        try {
            println("InviteViewModel: Loading test info for $testId")

            // Get test information from tests API
            when (val result = apiService.getTests()) {
                is ApiResult.Success -> {
                    if (result.data.isSuccess()) {
                        val test = result.data.data?.find { it.id == testId }
                        if (test != null) {
                            _testInfo.value = TestInfo(
                                id = test.id,
                                title = test.title,
                                description = "Take this test to discover more about yourself", // Default description
                                isPremium = test.isPremium
                            )
                        } else {
                            _error.value = "Test not found"
                        }
                    } else {
                        _error.value = "Failed to load test information"
                    }
                }
                is ApiResult.Error -> {
                    _error.value = "Error loading test: ${result.message}"
                }
                is ApiResult.Exception -> {
                    _error.value = "Network error: ${result.exception.message}"
                }
            }
        } catch (e: Exception) {
            println("InviteViewModel: Error loading test info: ${e.message}")
            _error.value = "Failed to load test information"
        }
    }

    private fun checkPremiumStatus() {
        viewModelScope.launch {
            try {
                // Use PremiumChecker for simpler premium check
                _userPremiumStatus.value = PremiumChecker.isPremium
                println("InviteViewModel: User premium status: ${_userPremiumStatus.value}")
            } catch (e: Exception) {
                println("InviteViewModel: Error checking premium status: ${e.message}")
                _userPremiumStatus.value = false // Default to non-premium if check fails
            }
        }
    }

    fun retryLoad(senderId: String, testId: String) {
        loadInviteData(senderId, testId)
    }

    fun acceptInviteAndStartTest(inviteId: String, testId: String) {
        viewModelScope.launch {
            _isAcceptingInvite.value = true
            _error.value = null

            try {
                println("InviteViewModel: Accepting invite $inviteId")

                when (val result = apiService.acceptInvite(inviteId)) {
                    is ApiResult.Success -> {
                        _inviteAccepted.value = true
                        _error.value = null
                        println("InviteViewModel: Invite accepted successfully")
                    }
                    is ApiResult.Error -> {
                        val errorMessage = result.message ?: "Failed to accept invite"
                        _error.value = errorMessage
                        println("InviteViewModel: Error accepting invite: $errorMessage")
                    }
                    is ApiResult.Exception -> {
                        val errorMessage = result.exception.message ?: "Network error accepting invite"
                        _error.value = errorMessage
                        println("InviteViewModel: Exception accepting invite: $errorMessage")
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unexpected error accepting invite"
                _error.value = errorMessage
                println("InviteViewModel: Unexpected error: $errorMessage")
            } finally {
                _isAcceptingInvite.value = false
            }
        }
    }

    fun rejectInvite(inviteId: String) {
        viewModelScope.launch {
            try {
                println("InviteViewModel: Rejecting invite $inviteId")

                when (val result = apiService.rejectInvite(inviteId)) {
                    is ApiResult.Success -> {
                        println("InviteViewModel: Invite rejected successfully")
                        // No need to update UI state since user will navigate back
                    }
                    is ApiResult.Error -> {
                        val errorMessage = result.message ?: "Failed to reject invite"
                        _error.value = errorMessage
                        println("InviteViewModel: Error rejecting invite: $errorMessage")
                    }
                    is ApiResult.Exception -> {
                        val errorMessage = result.exception.message ?: "Network error rejecting invite"
                        _error.value = errorMessage
                        println("InviteViewModel: Exception rejecting invite: $errorMessage")
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unexpected error rejecting invite"
                _error.value = errorMessage
                println("InviteViewModel: Unexpected error: $errorMessage")
            }
        }
    }
}