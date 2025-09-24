package com.keak.aishou.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TestDisplay(
    @SerialName("resultCard")
    val resultCard: String,
    @SerialName("color")
    val color: String,
    @SerialName("icon")
    val icon: String
)

@Serializable
data class Test(
    @SerialName("_id")
    val id: String,
    @SerialName("testId")
    val testId: String,
    @SerialName("title")
    val title: String,
    @SerialName("category")
    val category: String,
    @SerialName("activeVersion")
    val activeVersion: Int,
    @SerialName("isActive")
    val isActive: Boolean,
    @SerialName("isPremium")
    val isPremium: Boolean,
    @SerialName("display")
    val display: TestDisplay
)