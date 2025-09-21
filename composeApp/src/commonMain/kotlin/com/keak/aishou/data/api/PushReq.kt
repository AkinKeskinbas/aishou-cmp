package com.keak.aishou.data.api

import kotlinx.serialization.Serializable

@Serializable
data class PushReq(
    val playerId: String,
    val platform: String,
    val locale: String? = null,
    val timezone: String? = null
)