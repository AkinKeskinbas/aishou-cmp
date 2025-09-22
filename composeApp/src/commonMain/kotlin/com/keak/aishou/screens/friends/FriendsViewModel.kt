package com.keak.aishou.screens.friends

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

class FriendsViewModel(
    private val apiService: AishouApiService
) : ViewModel() {

    private val _friendsList = MutableStateFlow<List<FriendInfo>>(emptyList())
    val friendsList: StateFlow<List<FriendInfo>> = _friendsList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _sendRequestLoading = MutableStateFlow(false)
    val sendRequestLoading: StateFlow<Boolean> = _sendRequestLoading.asStateFlow()

    private val _sendRequestResult = MutableStateFlow<String?>(null)
    val sendRequestResult: StateFlow<String?> = _sendRequestResult.asStateFlow()

    init {
        loadFriends()
    }

    fun loadFriends() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = apiService.getFriendsList()) {
                is ApiResult.Success -> {
                    if (result.data.isSuccess()) {
                        _friendsList.value = result.data.data.orEmpty()
                    } else {
                        _error.value = "Failed to load friends"
                    }
                }

                is ApiResult.Error -> {
                    _error.value = result.message
                }

                is ApiResult.Exception -> {
                    _error.value = result.exception.message ?: "Unknown error occurred"
                }
            }

            _isLoading.value = false
        }
    }

    fun sendFriendRequest(userId: String? = null, displayName: String, tag: FriendTag? = null, message: String? = null) {
        viewModelScope.launch {
            _sendRequestLoading.value = true
            _sendRequestResult.value = null

            val request = SendFriendRequestReq(
                toUserId = null, // Send null for userId, backend will use displayName
                toUserDisplayName = displayName,
                tag = tag,
                message = message
            )

            when (val result = apiService.sendFriendRequest(request)) {
                is ApiResult.Success -> {
                    if (result.data.isSuccess()) {
                        _sendRequestResult.value = "Friend request sent successfully!"
                    } else {
                        _sendRequestResult.value = "Failed to send friend request"
                    }
                }

                is ApiResult.Error -> {
                    _sendRequestResult.value = result.message
                }

                is ApiResult.Exception -> {
                    _sendRequestResult.value = result.exception.message ?: "Unknown error occurred"
                }
            }

            _sendRequestLoading.value = false
        }
    }

    fun removeFriend(friendId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            when (val result = apiService.removeFriend(friendId)) {
                is ApiResult.Success -> {
                    if (result.data.isSuccess()) {
                        // Remove friend from local list
                        _friendsList.value = _friendsList.value.filter { it.userId != friendId }
                    } else {
                        _error.value = "Failed to remove friend"
                    }
                }

                is ApiResult.Error -> {
                    _error.value = result.message
                }

                is ApiResult.Exception -> {
                    _error.value = result.exception.message ?: "Unknown error occurred"
                }
            }

            _isLoading.value = false
        }
    }

    fun clearSendRequestResult() {
        _sendRequestResult.value = null
    }

    fun clearError() {
        _error.value = null
    }
}