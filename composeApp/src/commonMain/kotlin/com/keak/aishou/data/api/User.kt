package com.keak.aishou.data.api

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val _id: String,  // RevenueCat ID from client
    val displayName: String? ,
    val photoUrl: String? ,
    val lang: String = "en",
    val platform: String? ,  // "ios" or "android"
    val isAnonymous: Boolean ,
    val mbtiType: String? ,
    val zodiacSign: String?,
    val personalityAssessed: Boolean ,
    val solvedQuizzes: List<SolvedQuiz>,
    val createdAt: Long
)
@Serializable
data class SolvedQuiz(
    val testId: String,
    val version: Int,
    val solvedAt: Long,
    val submissionId: String?
)
