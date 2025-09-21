package com.keak.aishou.data.api

import kotlinx.serialization.Serializable

@Serializable
data class InviteCreateRequest(
    val friendId: String?,
    val testId: String,
    val version: Int
)