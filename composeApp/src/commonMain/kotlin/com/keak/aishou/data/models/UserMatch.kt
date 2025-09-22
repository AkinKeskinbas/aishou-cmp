package com.keak.aishou.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserMatch(
    val user1: UserInfo,
    val user2: UserInfo,
    val score: Int, // 0-100
    val summary: String,
    val explanations: List<String>,
    val matchingAnalysis: String,
    val chemistry: String
)

@Serializable
data class UserInfo(
    val userId: String,
    val displayName: String? = null,
    val mbtiType: String? = null,
    val zodiacSign: String? = null,
    val photoUrl: String? = null
)