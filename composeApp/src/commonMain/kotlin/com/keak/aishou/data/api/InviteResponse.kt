package com.keak.aishou.data.api

import kotlinx.serialization.Serializable

@Serializable
data class InviteData(
    val inviteId: String
)

@Serializable
data class InviteResponse(
    val status: String,
    val data: InviteData?
)