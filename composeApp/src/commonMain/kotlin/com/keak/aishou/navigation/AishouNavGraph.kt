package com.keak.aishou.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.keak.aishou.screens.allresults.AllResultsScreen
import com.keak.aishou.screens.allresults.AllResultsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.keak.aishou.screens.allresults.TestResultViewModel
import com.keak.aishou.screens.allresults.PersonalResultScreen
import com.keak.aishou.screens.allresults.TestResultScreen
import com.keak.aishou.screens.paywall.PaywallScreen
import com.keak.aishou.screens.homescreen.HomeScreen
import com.keak.aishou.screens.homescreen.HomeViewModel
import com.keak.aishou.screens.onboarding.OnBoardingScreenFourth
import com.keak.aishou.screens.onboarding.OnBoardingScreenThird
import com.keak.aishou.screens.onboarding.OnboardingScreen
import com.keak.aishou.screens.onboarding.OnboardingScreenSecond
import com.keak.aishou.screens.onboarding.ZodiacSelectionScreen
import com.keak.aishou.screens.onboarding.MBTIPreferenceScreen
import com.keak.aishou.screens.onboarding.MBTIPreferenceType
import com.keak.aishou.data.PersonalityDataManager
import com.keak.aishou.screens.quicktestscreen.QuickTestHomeScreen
import com.keak.aishou.screens.quicktestscreen.QuickTestHomeScreenViewModel
import com.keak.aishou.screens.quicktestscreen.QuizScreen
import com.keak.aishou.screens.quicktestscreen.QuizViewModel
import com.keak.aishou.screens.splashscreen.SplashScreen
import com.keak.aishou.screens.splashscreen.SplashViewModel
import com.keak.aishou.screens.reauth.ReAuthScreen
import com.keak.aishou.screens.reauth.ReAuthTestScreen
import com.keak.aishou.screens.friends.FriendsScreen
import com.keak.aishou.screens.notifications.NotificationsScreen
import com.keak.aishou.screens.invite.InviteScreen
import com.keak.aishou.screens.matching.UserMatchScreen
import com.keak.aishou.screens.profile.ProfileScreen
import com.keak.aishou.screens.thankyou.ThankYouScreen
import com.keak.aishou.data.models.UserMatch
import com.keak.aishou.data.models.UserInfo
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject

@Composable
fun AishouNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Routes = Routes.Splash,
    router: Router,
    navGraphBuilder: NavGraphBuilder.() -> Unit = {}
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination.route
    ) {
        navGraphBuilder.invoke(this)
        mainRoute(router)
    }
}

fun NavGraphBuilder.mainRoute(
    router: Router
) {
    composable(
        route = Routes.Splash.route
    ) {
        val splashViewModel: SplashViewModel = koinViewModel()
        SplashScreen(router = router, viewModel = splashViewModel)
    }
    composable(
        route = Routes.ReAuth.route
    ) {
        ReAuthScreen(
            onNavigateToHome = { router.goToHome() },
            onNavigateToSupport = {
                // TODO: Navigate to support screen or open support URL
                router.goToHome()
            }
        )
    }
    composable( // 🧪 DEBUG ONLY - REMOVE IN PRODUCTION
        route = Routes.ReAuthTest.route
    ) {
        ReAuthTestScreen(router = router)
    }
    composable(
        route = Routes.OnBoarding.route
    ) {
        OnboardingScreen(router = router)
    }
    composable(
        route = Routes.OnBoardingSecond.route
    ) {
        OnboardingScreenSecond(router = router)
    }
    composable(
        route = Routes.OnBoardingThird.route
    ) {
        OnBoardingScreenThird(router = router)
    }
    composable(
        route = Routes.OnBoardingFourth.route
    ) {
        OnBoardingScreenFourth(router = router)
    }
    composable(
        route = Routes.ZodiacSelection.route
    ) {
        val personalityDataManager: PersonalityDataManager = koinInject()
        ZodiacSelectionScreen(
            onZodiacSelected = { zodiacSign ->
                personalityDataManager.setZodiacSign(zodiacSign)
                router.goToMBTIPreference()
            }
        )
    }
    composable(
        route = Routes.MBTIPreference.route
    ) {
        val personalityDataManager: PersonalityDataManager = koinInject()
        val scope: CoroutineScope = koinInject()
        MBTIPreferenceScreen(
            onPreferenceSelected = { preferenceType, mbtiType ->
                when (preferenceType) {
                    MBTIPreferenceType.TAKE_TEST -> {
                        // Navigate to MBTI test using existing quiz system
                        router.goToQuizScreen("personality")
                    }
                    MBTIPreferenceType.MANUAL_ENTRY -> {
                        mbtiType?.let {
                            personalityDataManager.setMBTIType(it)

                            // Make API call to update personality data
                            scope.launch {
                                try {
                                    val result = personalityDataManager.updatePersonality()
                                    println("PersonalityUpdate: API call result: $result")

                                    // Navigate to home regardless of API result
                                    router.goToPaywall()
                                } catch (e: Exception) {
                                    println("PersonalityUpdate: Error updating personality: ${e.message}")
                                    // Navigate to home even on error
                                    router.goToHome()
                                }
                            }
                        }
                    }
                }
            }
        )
    }
    composable(
        route = Routes.Paywall.route
    ) {
        PaywallScreen(router = router)
    }
    composable(
        route = Routes.Home.route
    ) {
        val homeViewModel: HomeViewModel = koinViewModel()
        HomeScreen(router = router, homeViewModel)
    }
    composable(
        route = Routes.AllResults.route
    ) {
        val allResultsViewModel: AllResultsViewModel = koinViewModel()
        AllResultsScreen(router = router, viewModel = allResultsViewModel)
    }
    composable(
        route = Routes.QuicTests.route
    ) {
        val quickTestHomeScreenViewModel: QuickTestHomeScreenViewModel = koinViewModel()
        QuickTestHomeScreen(router = router, viewModel = quickTestHomeScreenViewModel)
    }
    composable(
        route = Routes.PersonelResult.route
    ) {
        PersonalResultScreen(router = router)
    }
    composable(
        route = Routes.TestResult.route,
        arguments = listOf(
            navArgument("testID") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val testID: String = backStackEntry.savedStateHandle.get<String>("testID") ?: ""
        val testResultViewModel: TestResultViewModel = koinViewModel()
        TestResultScreen(router = router, testID = testID, viewModel = testResultViewModel)
    }
    composable(
        route = Routes.QuizScreen.route,
        arguments = listOf(
            navArgument("quizID") { type = NavType.StringType },
            navArgument("senderId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
            navArgument("inviteId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val quizID: String = backStackEntry.savedStateHandle.get<String>("quizID") ?: ""
        val senderId: String? = backStackEntry.savedStateHandle.get<String>("senderId")
        val inviteId: String? = backStackEntry.savedStateHandle.get<String>("inviteId")
        val quizViewModel: QuizViewModel = koinViewModel()
        QuizScreen(router = router, quizID = quizID, senderId = senderId, inviteId = inviteId, viewModel = quizViewModel)
    }
    composable(
        route = Routes.QuickQuizScreen.route
    ) {
        val quizViewModel: QuizViewModel = koinViewModel()
        QuizScreen(router = router, quizID = null, senderId = null, inviteId = null, viewModel = quizViewModel)
    }
    composable(
        route = Routes.Friends.route
    ) {
        FriendsScreen(router = router)
    }
    composable(
        route = Routes.Notifications.route
    ) {
        NotificationsScreen(router = router)
    }
    composable(
        route = Routes.FriendRequest.route,
        arguments = listOf(
            navArgument("senderId") { type = NavType.StringType },
            navArgument("senderName") {
                type = NavType.StringType
                defaultValue = ""
            }
        )
    ) { backStackEntry ->

        val senderId = backStackEntry.savedStateHandle.get<String>("senderId") ?: ""
        val senderName = backStackEntry.savedStateHandle.get<String>("senderName") ?: ""

        // URL decode sender name
        val decodedName = senderName.replace("%20", " ").replace("%26", "&")

        // Navigate to notifications screen with the specific senderId to highlight
        NotificationsScreen(
            router = router,
            highlightSenderId = senderId,
            highlightSenderName = decodedName
        )
    }
    composable(
        route = Routes.Invite.route,
        arguments = listOf(
            navArgument("inviteId") { type = NavType.StringType },
            navArgument("senderId") { type = NavType.StringType },
            navArgument("testId") { type = NavType.StringType },
            navArgument("testTitle") {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument("senderName") {
                type = NavType.StringType
                defaultValue = "Unknown User"
            },
            navArgument("senderMbti") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->

        val inviteId = backStackEntry.savedStateHandle.get<String>("inviteId") ?: ""
        val senderId = backStackEntry.savedStateHandle.get<String>("senderId") ?: ""
        val testId = backStackEntry.savedStateHandle.get<String>("testId") ?: ""
        val testTitle = backStackEntry.savedStateHandle.get<String>("testTitle") ?: ""
        val senderName = backStackEntry.savedStateHandle.get<String>("senderName") ?: "Unknown User"
        val senderMbti = backStackEntry.savedStateHandle.get<String>("senderMbti")

        // URL decode parameters
        val decodedTitle = testTitle.replace("%20", " ").replace("%26", "&")
        val decodedSenderName = senderName.replace("%20", " ").replace("%26", "&")

        InviteScreen(
            inviteId = inviteId,
            senderId = senderId,
            testId = testId,
            testTitle = decodedTitle,
            senderName = decodedSenderName,
            senderMbti = senderMbti,
            router = router
        )
    }
    composable(
        route = Routes.UserMatch.route,
        arguments = listOf(
            navArgument("testID") { type = NavType.StringType },
            navArgument("friendId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val testID: String = backStackEntry.savedStateHandle.get<String>("testID") ?: ""
        val friendId: String? = backStackEntry.savedStateHandle.get<String>("friendId")

        UserMatchScreen(
            testID = testID,
            friendId = friendId,
            router = router
        )
    }

    composable(
        route = Routes.Profile.route
    ) {
        ProfileScreen(
            router = router
        )
    }

    composable(
        route = Routes.ThankYou.route,
        arguments = listOf(
            navArgument("isFromInvite") {
                type = NavType.BoolType
                defaultValue = false
            }
        )
    ) { backStackEntry ->
        val isFromInvite: Boolean = backStackEntry.savedStateHandle.get<Boolean>("isFromInvite") ?: false

        ThankYouScreen(
            router = router,
            isFromInvite = isFromInvite
        )
    }
}
