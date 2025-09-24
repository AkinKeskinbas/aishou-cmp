package com.keak.aishou.data.api

import kotlinx.serialization.Serializable

@Serializable
data class ProfileUpdateRequest(
    val displayName: String
)