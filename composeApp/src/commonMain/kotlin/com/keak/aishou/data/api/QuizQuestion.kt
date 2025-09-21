package com.keak.aishou.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuizChoice(
    @SerialName("key")
    val key: String,
    @SerialName("text")
    val text: String
)

@Serializable
data class QuizWeights(
    // Dynamic map for different personality traits and their weights
    // e.g., "extrovert": 3, "introvert": 2, etc.
    val weights: Map<String, Int> = emptyMap()
) {
    // Helper to get all weight entries as a map
    fun getAllWeights(): Map<String, Int> = weights
}

@Serializable
data class QuizQuestion(
    @SerialName("testId")
    val testId: String,
    @SerialName("version")
    val version: Int,
    @SerialName("index")
    val index: Int,
    @SerialName("text")
    val text: String,
    @SerialName("choices")
    val choices: List<QuizChoice>,
    @SerialName("weights")
    val weights: Map<String, Map<String, Int>>, // Choice key -> trait weights
    @SerialName("locale")
    val locale: String
)