package com.keak.aishou.navigation

sealed class Routes(val route: String = "") {
    data object Splash : Routes("Splash")
    data object OnBoarding : Routes("OnBoarding")
    data object OnBoardingSecond : Routes("OnBoardingSecond")
    data object OnBoardingThird : Routes("OnBoardingThird")
    data object OnBoardingFourth : Routes("OnBoardingFourth")
    data object ZodiacSelection : Routes("ZodiacSelection")
    data object MBTIPreference : Routes("MBTIPreference")
    data object Home : Routes("Home")
    data object AllResults : Routes("AllResults")
    data object QuicTests : Routes("QuicTests")
    data object PersonelResult : Routes("PersonelResult")
    data object TestResult : Routes("TestResults/{testID}") {
        fun passTestID(testID: String): String {
            return "TestResults/$testID"
        }
    }
    data object QuizScreen : Routes("QuizScreen/{quizID}") {
        fun passQuizID(quizID: String): String {
            return "QuizScreen/$quizID"
        }
    }
    data object QuickQuizScreen : Routes("QuickQuizScreen")

    data object Paywall : Routes("Paywall")
}