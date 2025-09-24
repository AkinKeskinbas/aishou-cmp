package com.keak.aishou.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keak.aishou.network.AishouApiService
import com.keak.aishou.network.ApiResult
import com.keak.aishou.data.models.*
import com.keak.aishou.response.isSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class NotificationsViewModel(
    private val apiService: AishouApiService
) : ViewModel() {

    private val _friendRequests = MutableStateFlow<List<RequestWithSenderInfo>>(emptyList())
    val friendRequests: StateFlow<List<RequestWithSenderInfo>> = _friendRequests.asStateFlow()

    private val _testInvites = MutableStateFlow<List<TestInvite>>(emptyList())
    val testInvites: StateFlow<List<TestInvite>> = _testInvites.asStateFlow()

    private val _unreadCount = MutableStateFlow(0L)
    val unreadCount: StateFlow<Long> = _unreadCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _respondingToRequest = MutableStateFlow<String?>(null)
    val respondingToRequest: StateFlow<String?> = _respondingToRequest.asStateFlow()

    init {
        loadNotifications()
        loadTestInvitations()
        loadUnreadCount()
    }

    fun loadNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _error.value = null

            // Load received requests
            when (val result = apiService.getReceivedRequests()) {
                is ApiResult.Success -> {
                    if (result.data.isSuccess()) {
                        val requests = result.data.data.orEmpty()
                        println("NotificationsViewModel: Loaded ${requests.size} friend requests")
                        requests.forEachIndexed { index, requestWithSender ->
                            println("NotificationsViewModel: Request $index:")
                            println("  - Request _id: ${requestWithSender.request._id}")
                            println("  - Request id: ${requestWithSender.request.id}")
                            println("  - Actual ID: ${requestWithSender.request.getRequestId()}")
                            println("  - From User: ${requestWithSender.request.fromUserId}")
                            println("  - To User: ${requestWithSender.request.toUserId}")
                            println("  - Sender ID: ${requestWithSender.sender.userId}")
                            println("  - Sender Name: ${requestWithSender.sender.displayName}")
                        }
                        _friendRequests.value = requests
                    } else {
                        _error.value = "Failed to load friend requests"
                    }
                }
                is ApiResult.Error -> {
                    if (result.code == 500) {
                        // Server error - set empty list and no error message to avoid user confusion
                        _friendRequests.value = emptyList()
                        println("NotificationsViewModel: Server error (500) loading requests - showing empty state")
                    } else {
                        _error.value = result.message
                    }
                }
                is ApiResult.Exception -> {
                    // Network exception - show empty state
                    _friendRequests.value = emptyList()
                    println("NotificationsViewModel: Exception loading requests - showing empty state: ${result.exception.message}")
                }
            }


            _isLoading.value = false
        }
    }

    fun loadTestInvitations() {
        viewModelScope.launch(Dispatchers.IO) {
            // Load test invitations
            when (val result = apiService.getReceivedInvites()) {
                is ApiResult.Success -> {
                    if (result.data.isSuccess()) {
                        val allInvites = result.data.data.orEmpty()
                        // Filter out rejected and accepted invites, only show pending ones
                        val pendingInvites = allInvites.filter { invite ->
                            invite.status == null || invite.status == "pending"
                        }
                        println("NotificationsViewModel: Loaded ${allInvites.size} total invitations, ${pendingInvites.size} pending")
                        pendingInvites.forEachIndexed { index, invite ->
                            println("NotificationsViewModel: Pending Invite $index:")
                            println("  - Invite ID: ${invite.inviteId}")
                            println("  - Test ID: ${invite.testId}")
                            println("  - Test Title: ${invite.testTitle}")
                            println("  - Sender: ${invite.senderName}")
                            println("  - Status: ${invite.status}")
                        }
                        _testInvites.value = pendingInvites
                    } else {
                        println("NotificationsViewModel: Failed to load test invitations")
                    }
                }
                is ApiResult.Error -> {
                    if (result.code == 500) {
                        // Server error - set empty list and no error message to avoid user confusion
                        _testInvites.value = emptyList()
                        println("NotificationsViewModel: Server error (500) loading invites - showing empty state")
                    } else {
                        println("NotificationsViewModel: Error loading invites: ${result.message}")
                    }
                }
                is ApiResult.Exception -> {
                    // Network exception - show empty state
                    _testInvites.value = emptyList()
                    println("NotificationsViewModel: Exception loading invites - showing empty state: ${result.exception.message}")
                }
            }
        }
    }

    fun loadUnreadCount() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = apiService.getUnreadRequestsCount()) {
                is ApiResult.Success -> {
                    if (result.data.isSuccess() && result.data.data != null) {
                        _unreadCount.value = result.data.data.count
                    }
                }
                is ApiResult.Error -> {
                    // Silently fail for unread count
                }
                is ApiResult.Exception -> {
                    // Silently fail for unread count
                }
            }
        }
    }

    fun acceptFriendRequest(requestId: String, tag: FriendTag? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _respondingToRequest.value = requestId

            val response = RespondFriendRequestReq(
                accept = true,
                tag = tag
            )

            when (val result = apiService.respondToFriendRequest(requestId, response)) {
                is ApiResult.Success -> {
                    if (result.data.isSuccess()) {
                        // Remove request from list
                        _friendRequests.value = _friendRequests.value.filter {
                            it.request.getRequestId() != requestId
                        }
                        // Update unread count
                        loadUnreadCount()
                    } else {
                        _error.value = "Failed to accept friend request"
                    }
                }
                is ApiResult.Error -> {
                    _error.value = result.message
                }
                is ApiResult.Exception -> {
                    _error.value = result.exception.message ?: "Unknown error occurred"
                }
            }

            _respondingToRequest.value = null
        }
    }

    fun rejectFriendRequest(requestId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _respondingToRequest.value = requestId

            val response = RespondFriendRequestReq(
                accept = false,
                tag = null
            )

            when (val result = apiService.respondToFriendRequest(requestId, response)) {
                is ApiResult.Success -> {
                    if (result.data.isSuccess()) {
                        // Remove request from list
                        _friendRequests.value = _friendRequests.value.filter {
                            it.request.getRequestId() != requestId
                        }
                        // Update unread count
                        loadUnreadCount()
                    } else {
                        _error.value = "Failed to reject friend request"
                    }
                }
                is ApiResult.Error -> {
                    _error.value = result.message
                }
                is ApiResult.Exception -> {
                    _error.value = result.exception.message ?: "Unknown error occurred"
                }
            }

            _respondingToRequest.value = null
        }
    }

    fun markRequestAsRead(requestId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = apiService.markRequestAsRead(requestId)) {
                is ApiResult.Success -> {
                    if (result.data.isSuccess()) {
                        // Update local state
                        _friendRequests.value = _friendRequests.value.map { requestWithSender ->
                            if (requestWithSender.request._id == requestId) {
                                requestWithSender.copy(
                                    request = requestWithSender.request.copy(isRead = true)
                                )
                            } else {
                                requestWithSender
                            }
                        }
                        // Update unread count
                        loadUnreadCount()
                    }
                }
                is ApiResult.Error -> {
                    // Silently fail for mark as read
                }
                is ApiResult.Exception -> {
                    // Silently fail for mark as read
                }
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = apiService.markAllRequestsAsRead()) {
                is ApiResult.Success -> {
                    if (result.data.isSuccess()) {
                        // Update local state
                        _friendRequests.value = _friendRequests.value.map { requestWithSender ->
                            requestWithSender.copy(
                                request = requestWithSender.request.copy(isRead = true)
                            )
                        }
                        _unreadCount.value = 0
                    }
                }
                is ApiResult.Error -> {
                    // Silently fail for mark all as read
                }
                is ApiResult.Exception -> {
                    // Silently fail for mark all as read
                }
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun onTestInviteClicked(testInvite: TestInvite, onNavigate: (String, String, String, String) -> Unit) {
        // Navigate to invite screen with the test invite data
        onNavigate(
            testInvite.inviteId,
            testInvite.fromUserId,
            testInvite.testId,
            testInvite.testTitle
        )
    }
}