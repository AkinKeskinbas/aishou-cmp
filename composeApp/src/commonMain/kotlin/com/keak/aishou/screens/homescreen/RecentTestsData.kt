package com.keak.aishou.screens.homescreen

import androidx.compose.ui.graphics.Color
import com.keak.aishou.screens.quicktestscreen.QuizType

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
