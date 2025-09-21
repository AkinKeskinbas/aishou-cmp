package com.keak.aishou.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonalityAssessRequest(
    @SerialName("testId")
    val testId: String,
    @SerialName("version")
    val version: Int,
    @SerialName("answers")
    val answers: Map<String, String>
)