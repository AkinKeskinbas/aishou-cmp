package com.keak.aishou.data.api

import kotlinx.serialization.Serializable

@Serializable
data class TestResultResponse(
    val testId: String,
    val soloResult: SoloResult? = null,
    val compatibilityResults: List<CompatibilityResult> = emptyList(),
    val resultType: String, // "solo", "compatibility", "both", "none"
    val myDisplayName: String
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
    val testId: String? = null,
    val friendId: String? = null,
    val friendInfo: FriendInfo? = null, // Friend's profile information
    val myInfo: FriendInfo? = null, // Current user's profile information
    val score: Int? = null,
    val summary: String? = null,
    val chemistry: String? = null, // New field for chemistry description
    val explanations: List<CompatibilityExplanation>? = null, // Detailed explanations
    val matchingAnalysis: MatchingAnalysis? = null, // MBTI and Zodiac matching details
    val createdAt: Long? = null
)

@Serializable
data class FriendInfo(
    val userId: String? = null,
    val displayName: String? = null,
    val mbtiType: String? = null,
    val zodiacSign: String? = null
)

@Serializable
data class CompatibilityExplanation(
    val topic: String? = null,
    val detail: String? = null,
)

@Serializable
data class MatchingAnalysis(
    val mbtiMatch: MBTICompatibility? = null,
    val zodiacMatch: ZodiacCompatibility? = null,
    val overallCompatibility: String? = null
)

@Serializable
data class MBTICompatibility(
    val typeA: String? = null,
    val typeB: String? = null,
    val compatibilityScore: Int? = null,
    val explanation: String? = null,
    val strengths: List<String>?,
    val challenges: List<String>?
)

@Serializable
data class ZodiacCompatibility(
    val signA: String? = null,
    val signB: String? = null,
    val compatibilityScore: Int? = null,
    val strengths: List<String>?,
    val challenges: List<String>?,
    val explanation: String? = null,
    val elementInteraction: String? = null,
)

enum class ResultType(val value: String) {
    SOLO("solo"),
    COMPATIBILITY("compatibility"),
    BOTH("both"),
    NONE("none");

    companion object {
        fun fromString(value: String): ResultType {
            return entries.find { it.value == value } ?: NONE
        }
    }
}