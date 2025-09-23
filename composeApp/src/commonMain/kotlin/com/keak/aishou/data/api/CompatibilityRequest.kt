package com.keak.aishou.data.api

import kotlinx.serialization.Serializable

@Serializable
data class CompatibilityRequest(
    val testId: String,
    val version: Int,
    val friendId: String
) {
    // Add validation to help debug
    init {
        require(testId.isNotBlank()) { "testId cannot be blank" }
        require(friendId.isNotBlank()) { "friendId cannot be blank" }
        require(version > 0) { "version must be positive" }
    }
}