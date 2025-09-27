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

    override fun goToQuizScreenWithSender(quizID: String, senderId: String) {
        navigateArg(Routes.QuizScreen.passQuizIDWithSender(quizID, senderId))
    }

    override fun goToQuizScreenWithInvite(quizID: String, senderId: String, inviteId: String) {
        navigateArg(Routes.QuizScreen.passQuizIDWithInvite(quizID, senderId, inviteId))
    }

    override fun goToQuickQuizScreen() {
        navigate(Routes.QuickQuizScreen)
    }

    override fun goToDefaultQuizScreen() {
        // Quick start with a predefined popular test
        // Using a specific test ID for the most popular/default test
        // This could be made configurable via remote config in the future
        val defaultTestId = "674f6a8ee53de6825e45d2ce" // Default popular test ID
        navigateArg(Routes.QuizScreen.passQuizID(defaultTestId))
    }

    override fun goToTestResultScreen(testID: String) {
        navigateArg(Routes.TestResult.passTestID(testID))
    }

    override fun goToSplash() {
        navigate(Routes.Splash, removeFromHistory = true, singleTop = true)
    }

    override fun goToReAuth() {
        navigate(Routes.ReAuth, removeFromHistory = true, singleTop = true)
    }


    override fun goToPaywall() {
        navigate(Routes.Paywall)
    }

    override fun goToPaywallWithReturn(returnTo: String) {
        navHostController.navigate(Routes.Paywall.withReturnTo(returnTo))
    }

    override fun navigateToRoute(route: String) {
        navHostController.navigate(route) {
            // Remove paywall from back stack
            popUpTo(Routes.Paywall.route) {
                inclusive = true
            }
        }
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

    override fun goToFriends() {
        navigate(Routes.Friends)
    }

    override fun goToNotifications() {
        navigate(Routes.Notifications)
    }

    override fun goToFriendRequest(senderId: String, senderName: String) {
        navigateArg(Routes.FriendRequest.passFriendRequestData(senderId, senderName))
    }

    override fun goToInvite(inviteId: String, senderId: String, testId: String, testTitle: String, senderName: String, senderMbti: String?) {
        navigateArg(Routes.Invite.passInviteData(inviteId, senderId, testId, testTitle, senderName, senderMbti))
    }

    override fun goToUserMatch(testID: String) {
        navigateArg(Routes.UserMatch.passTestID(testID))
    }

    override fun goToUserMatchWithFriend(testID: String, friendId: String) {
        navigateArg(Routes.UserMatch.passTestIDWithFriend(testID, friendId))
    }

    override fun goToProfile() {
        navigate(Routes.Profile)
    }

    override fun goToThankYou(isFromInvite: Boolean) {
        navigateArg(Routes.ThankYou.passFromInvite(isFromInvite))
    }

    override fun goToMBTIResult() {
        navigate(Routes.MBTIResult)
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