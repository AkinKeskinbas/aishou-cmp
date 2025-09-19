package com.keak.aishou.navigation

interface Router {
    fun goBack()
    fun goToHome()
    fun goToAllResultScreen()
    fun goToQuickTestScreen()
    fun goToPersonalResultScreen()
    fun goToQuizScreen(quizID: String)
    fun goToTestResultScreen(testID: String)
    fun goToSplash()
    fun goToPaywall()
    fun goToOnBoarding()
    fun goToOnBoardingSecond()
    fun goToOnBoardingThird()
    fun goToOnBoardingFourth()
    fun goToQuickTest()

}