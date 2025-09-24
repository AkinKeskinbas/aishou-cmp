package com.keak.aishou.data.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class UserProfileResponse(
    val userId: String? = null,
    val displayName: String? = null,
    val email: String? = null,
    val mbtiType: String? = null,
    val zodiacSign: String? = null,
    val personalityAssessed: Boolean = false,
    val lang: String? = null,
    val soloQuizzes: List<SoloQuizResult> = emptyList(),
    val matchQuizzes: List<CompatibilityResult> = emptyList(),
    val totalQuizzes: Int = 0,
    val hasChangedName: Boolean = false
) {
    // Helper properties to maintain compatibility with existing code
    val mbti: String? get() = mbtiType
    val zodiac: String? get() = zodiacSign
    val solvedTests: List<SolvedTest>
        get() = (soloQuizzes.map { it.toSolvedTest("solo") } + matchQuizzes.map { it.toSolvedTest() }).take(3)
}

@Serializable
data class SoloQuizResult(
    val submissionId: String? = null,
    val testId: String? = null,
    val version: Int? = null,
    val totalScore: Int? = null,
    val completedAt: Long? = null,
    val type: String? = null
) {
    fun toSolvedTest(quizType: String) = SolvedTest(
        id = submissionId,
        type = quizType,
        title = testId,
        description = "Test completed",
        solvedAt = completedAt?.toString(),
        score = totalScore
    )
}

// Extension function for CompatibilityResult to convert to SolvedTest
fun CompatibilityResult.toSolvedTest() = SolvedTest(
    id = compatibilityId,
    type = "match",
    title = "Compatibility Test",  // Or use testId if available
    description = summary ?: "Compatibility test completed",
    solvedAt = createdAt?.toString(),
    score = score
)

@Serializable
data class SolvedTest(
    val id: String? = null,
    val type: String? = null, // "solo" or "match"
    val title: String? = null,
    val description: String? = null,
    val solvedAt: String? = null,
    val score: Int? = null
)