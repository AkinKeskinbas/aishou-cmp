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
                        router.goToQuizScreen("personality-full-v1")
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
                                    router.goToHome()
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
            navArgument("quizID") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val quizID: String = backStackEntry.savedStateHandle.get<String>("quizID") ?: ""
        val quizViewModel: QuizViewModel = koinViewModel()
        QuizScreen(router = router, quizID = quizID, viewModel = quizViewModel)
    }
    composable(
        route = Routes.QuickQuizScreen.route
    ) {
        val quizViewModel: QuizViewModel = koinViewModel()
        QuizScreen(router = router, quizID = null, viewModel = quizViewModel)
    }
}
