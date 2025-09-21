package com.keak.aishou.navigation

import androidx.navigation.NavHostController

class RouterImpl(
    private val navHostController: NavHostController,
    private val startDestination: String = Routes.Splash.route
): Router {
    override fun goBack() {
        navHostController.apply {
            popBackStack()
        }
    }

    override fun goToHome() {
        navigate(Routes.Home, removeFromHistory = true, singleTop = true)
    }

    override fun goToAllResultScreen() {
        navigate(Routes.AllResults)
    }

    override fun goToQuickTestScreen() {
        navigate(Routes.QuicTests)
    }

    override fun goToPersonalResultScreen() {
        navigate(Routes.PersonelResult)
    }

    override fun goToQuizScreen(quizID: String) {
        navigateArg(Routes.QuizScreen.passQuizID(quizID))
    }

    override fun goToQuickQuizScreen() {
        navigate(Routes.QuickQuizScreen)
    }

    override fun goToTestResultScreen(testID: String) {
        navigateArg(Routes.TestResult.passTestID(testID))
    }

    override fun goToSplash() {
        navigate(Routes.Splash, removeFromHistory = true, singleTop = true)
    }

    override fun goToPaywall() {
        navigate(Routes.Paywall)
    }

    override fun goToOnBoarding() {
        navigate(Routes.OnBoarding)
    }

    override fun goToOnBoardingSecond() {
        navigate(Routes.OnBoardingSecond)
    }

    override fun goToOnBoardingThird() {
        navigate(Routes.OnBoardingThird)
    }

    override fun goToOnBoardingFourth() {
        navigate(Routes.OnBoardingFourth)
    }

    override fun goToZodiacSelection() {
        navigate(Routes.ZodiacSelection)
    }

    override fun goToMBTIPreference() {
        navigate(Routes.MBTIPreference)
    }

    override fun goToQuickTest() {
        navigate(Routes.QuicTests)
    }

    private fun navigate(
        routes: Routes,
        removeFromHistory: Boolean = false,
        singleTop: Boolean = false,
        removeFromBackStack: Boolean = false
    ) {
        navHostController.apply {
            navigate(routes.route) {
                if (removeFromHistory) {
                    if (singleTop) {
                        popUpTo(startDestination) {
                            inclusive = true
                        }
                    } else {
                        popUpTo(navHostController.graph.startDestinationRoute ?: "home") {
                            saveState = false
                        }
                    }
                } else if (removeFromBackStack) {
                    popUpTo(routes.route) {
                        inclusive = true
                    }
                } else {
                    restoreState = true
                }
                launchSingleTop = singleTop
            }
        }
    }
    private fun navigateArg(
        routes: String,
        removeFromHistory: Boolean = false,
        singleTop: Boolean = false,
        removeFromBackStack: Boolean = false
    ) {
        navHostController.apply {
            navigate(routes) {
                if (removeFromHistory) {
                    if (singleTop) {
                        popUpTo(startDestination) {
                            inclusive = true
                        }
                    } else {
                        popUpTo(navHostController.graph.startDestinationRoute ?: "home") {
                            saveState = false
                        }
                    }
                } else if (removeFromBackStack) {
                    popUpTo(routes) {
                        inclusive = true
                    }
                } else {
                    restoreState = true
                }
                launchSingleTop = singleTop
            }
        }
    }
}