package com.keak.aishou.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonalityUpdateRequest(
    @SerialName("mbtiType")
    val mbtiType: String?,
    @SerialName("zodiacSign")
    val zodiacSign: String?
)