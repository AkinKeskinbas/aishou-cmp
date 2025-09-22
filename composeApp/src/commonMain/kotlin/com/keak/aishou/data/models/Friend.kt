package com.keak.aishou.data.models

import kotlinx.serialization.Serializable

// Backend API Models
@Serializable
data class FriendRequest(
    val _id: String? = null,
    val id: String? = null, // Alternative ID field
    val fromUserId: String,
    val toUserId: String,
    val status: FriendRequestStatus = FriendRequestStatus.PENDING,
    val tag: FriendTag? = null,
    val message: String? = null,
    val isRead: Boolean = false,
    val createdAt: Long = 0L,
    val respondedAt: Long? = null
) {
    // Helper to get actual ID from either field
    fun getRequestId(): String? = _id ?: id
}

@Serializable
data class Friendship(
    val _id: String? = null,
    val userAId: String,
    val userBId: String,
    val tagA: FriendTag? = null,
    val tagB: FriendTag? = null,
    val createdAt: Long = 0L
)

@Serializable
enum class FriendRequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}


@Serializable
enum class FriendTag {
    FRIEND,
    FAMILY,
    COLLEAGUE,
    PARTNER
}

// Request Models
@Serializable
data class SendFriendRequestReq(
    val toUserId: String? = null,
    val toUserDisplayName: String,
    val tag: FriendTag? = null,
    val message: String? = null
)

@Serializable
data class RespondFriendRequestReq(
    val accept: Boolean,
    val tag: FriendTag? = null
)

// Response Models
@Serializable
data class FriendInfo(
    val userId: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val tag: String? = null,
    val mbtiType: String? = null,
    val zodiacSign: String? = null,
    val friendsSince: Long = 0L
)

@Serializable
data class RequestWithSenderInfo(
    val request: FriendRequest,
    val sender: SenderInfo
)

@Serializable
data class SenderInfo(
    val userId: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val mbtiType: String? = null,
    val zodiacSign: String? = null
)

@Serializable
data class RequestWithReceiverInfo(
    val request: FriendRequest,
    val receiver: ReceiverInfo
)

@Serializable
data class ReceiverInfo(
    val userId: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val mbtiType: String? = null,
    val zodiacSign: String? = null
)

@Serializable
data class UnreadCount(
    val count: Long
)

// These response models now use BaseResponse wrapper pattern
// All API calls should return BaseResponse<DataType> from backend

// Legacy models for backward compatibility
@Serializable
data class Friend(
    val id: String,
    val name: String,
    val mbti: String? = null,
    val zodiac: String? = null,
    val avatarUrl: String? = null,
    val status: FriendStatus = FriendStatus.ACTIVE,
    val addedAt: Long = 0L
)

@Serializable
enum class FriendStatus {
    ACTIVE,
    BLOCKED,
    PENDING
}

@Serializable
data class Notification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val data: Map<String, String> = emptyMap(),
    val isRead: Boolean = false,
    val createdAt: Long = 0L
)

@Serializable
enum class NotificationType {
    FRIEND_REQUEST,
    FRIEND_ACCEPTED,
    QUIZ_INVITE,
    QUIZ_RESULT,
    SYSTEM_ANNOUNCEMENT
}

// Extension functions for convenience
fun Friend.getDisplayName(): String = name.ifBlank { "User $id" }

fun Friend.getPersonalityInfo(): String {
    val parts = mutableListOf<String>()
    mbti?.let { parts.add(it) }
    zodiac?.let { parts.add(it) }
    return parts.joinToString(" • ")
}

fun FriendRequest.isExpired(expirationDays: Int = 30): Boolean {
    // TODO: implement with proper time source
    return false
}

fun Notification.getTimeAgo(): String {
    // TODO: implement with proper time source
    return "Just now"
}