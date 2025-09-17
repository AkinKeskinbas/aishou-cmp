package com.keak.aishou.navigation

interface Router {
    fun goBack()
    fun goToHome()
    fun goToAllResultScreen()
    fun goToQuickTestScreen()
    fun goToQuizScreen(quizID: String)
    fun goToTestResultScreen(testID: String)
    fun goToSplash()
    fun goToPaywall()

}