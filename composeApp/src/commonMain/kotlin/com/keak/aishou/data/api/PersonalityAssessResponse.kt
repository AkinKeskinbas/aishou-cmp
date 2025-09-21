package com.keak.aishou.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonalityAssessResponse(
    @SerialName("mbtiType")
    val mbtiType: String,
    @SerialName("zodiacSign")
    val zodiacSign: String,
    @SerialName("personalityInsights")
    val personalityInsights: String? = null,
    @SerialName("assessmentId")
    val assessmentId: String? = null
)