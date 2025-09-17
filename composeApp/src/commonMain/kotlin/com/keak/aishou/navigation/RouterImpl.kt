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

    override fun goToQuizScreen(quizID: String) {
        navigateArg(Routes.QuizScreen.passQuizID(quizID))
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