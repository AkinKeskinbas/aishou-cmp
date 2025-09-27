package com.keak.aishou.data.api

import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuizSubmissionRequest(
    @SerialName("answers")
    val answers: Map<String, String>
) {
    companion object {
        fun create(answers: Map<String, String>): QuizSubmissionRequest {
            return QuizSubmissionRequest(
                answers = answers.mapValues { it.value.uppercase() }
            )
        }
    }

    override fun toString(): String {
        return "QuizSubmissionRequest(answers=$answers)"
    }
}

@Serializable
data class Submission(
    @SerialName("_id")
    val id: String? = null,
    @SerialName("testId")
    val testId: String,
    @SerialName("version")
    val version: Int,
    @SerialName("uid")
    val uid: String,
    @SerialName("answers")
    val answers: Map<String, String>,
    @SerialName("scoreVector")
    val scoreVector: List<Int>? = null,
    @SerialName("totalScore")
    val totalScore: Int? = null,
    @SerialName("personalizedInsights")
    val personalizedInsights: String? = null,
    @SerialName("scoreDescription")
    val scoreDescription: String? = null,
    @SerialName("completedAt")
    val completedAt: Long = Clock.System.now().toEpochMilliseconds()
)