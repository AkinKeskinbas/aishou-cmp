package com.keak.aishou.data.api

import kotlinx.serialization.Serializable

@Serializable
data class TestResultResponse(
    val testId: String,
    val soloResult: SoloResult? = null,
    val compatibilityResults: List<CompatibilityResult> = emptyList(),
    val resultType: String // "solo", "compatibility", "both", "none"
)

@Serializable
data class SoloResult(
    val submissionId: String? = null,
    val totalScore: Int = 0,
    val personalizedInsights: String? = null,
    val scoreVector: List<Int> = emptyList(),
    val completedAt: Long? = null
)

@Serializable
data class CompatibilityResult(
    val compatibilityId: String? = null,
    val friendId: String? = null,
    val score: Int = 0,
    val summary: String? = null,
    val createdAt: Long? = null
)

enum class ResultType(val value: String) {
    SOLO("solo"),
    COMPATIBILITY("compatibility"),
    BOTH("both"),
    NONE("none");

    companion object {
        fun fromString(value: String): ResultType {
            return values().find { it.value == value } ?: NONE
        }
    }
}