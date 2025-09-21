package com.keak.aishou.data.api

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    val mbtiType: String? = null,
    val zodiacSign: String? = null,
    val personalityAssessed: Boolean = false,
    val lang: String? = null,
    val soloQuizzes: List<QuizResult> = emptyList(),
    val matchQuizzes: List<QuizResult> = emptyList(),
    val totalQuizzes: Int = 0
) {
    // Helper properties to maintain compatibility with existing code
    val mbti: String? get() = mbtiType
    val zodiac: String? get() = zodiacSign
    val solvedTests: List<SolvedTest>
        get() = (soloQuizzes.map { it.toSolvedTest("solo") } + matchQuizzes.map { it.toSolvedTest("match") }).take(3)
}

@Serializable
data class QuizResult(
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

@Serializable
data class SolvedTest(
    val id: String? = null,
    val type: String? = null, // "solo" or "match"
    val title: String? = null,
    val description: String? = null,
    val solvedAt: String? = null,
    val score: Int? = null
)