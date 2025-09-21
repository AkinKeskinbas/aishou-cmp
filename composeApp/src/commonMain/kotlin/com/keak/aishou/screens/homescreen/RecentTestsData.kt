package com.keak.aishou.screens.homescreen

import androidx.compose.ui.graphics.Color
import com.keak.aishou.data.api.QuizType

data class RecentTestsData(
    val testerName: String,
    val testerMbti: String,
    val testResult: String,
    val testerUserId: String,
    val testerType: TesterType,
    val resultBg: Color,
    val testID: String,
    val testType: QuizType
)
