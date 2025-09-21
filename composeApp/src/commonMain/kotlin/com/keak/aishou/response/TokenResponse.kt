package com.keak.aishou.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    @SerialName("token")
    val token: String,
    @SerialName("refreshToken")
    val refreshToken: String
)