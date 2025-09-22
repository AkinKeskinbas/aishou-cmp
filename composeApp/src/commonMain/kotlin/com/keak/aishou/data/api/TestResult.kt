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
    val compatibilityId: String,
    val friendId: String,
    val friendInfo: FriendInfo?, // Friend's profile information
    val score: Int,
    val summary: String,
    val chemistry: String?, // New field for chemistry description
    val explanations: List<CompatibilityExplanation>?, // Detailed explanations
    val matchingAnalysis: MatchingAnalysis?, // MBTI and Zodiac matching details
    val createdAt: Long
)

@Serializable
data class FriendInfo(
    val userId: String,
    val displayName: String?,
    val mbtiType: String?,
    val zodiacSign: String?
)

@Serializable
data class CompatibilityExplanation(
    val category: String,
    val description: String,
    val impact: String? = null
)

@Serializable
data class MatchingAnalysis(
    val mbtiCompatibility: MBTICompatibility?,
    val zodiacCompatibility: ZodiacCompatibility?,
    val overallAnalysis: String?
)

@Serializable
data class MBTICompatibility(
    val user1Type: String,
    val user2Type: String,
    val compatibilityScore: Int,
    val description: String
)

@Serializable
data class ZodiacCompatibility(
    val user1Sign: String,
    val user2Sign: String,
    val compatibilityScore: Int,
    val description: String
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