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
import com.keak.aishou.screens.allresults.PersonalResultScreen
import com.keak.aishou.screens.allresults.TestResultScreen
import com.keak.aishou.screens.paywall.PaywallScreen
import com.keak.aishou.screens.homescreen.HomeScreen
import com.keak.aishou.screens.homescreen.HomeViewModel
import com.keak.aishou.screens.onboarding.OnBoardingScreenFourth
import com.keak.aishou.screens.onboarding.OnBoardingScreenThird
import com.keak.aishou.screens.onboarding.OnboardingScreen
import com.keak.aishou.screens.onboarding.OnboardingScreenSecond
import com.keak.aishou.screens.quicktestscreen.QuickTestHomeScreen
import com.keak.aishou.screens.quicktestscreen.QuickTestHomeScreenViewModel
import com.keak.aishou.screens.quicktestscreen.QuizScreen
import com.keak.aishou.screens.splashscreen.SplashScreen
import com.keak.aishou.screens.splashscreen.SplashViewModel
import org.koin.compose.viewmodel.koinViewModel

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
        AllResultsScreen(router = router)
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
        TestResultScreen(router = router, testID = testID)
    }
    composable(
        route = Routes.QuizScreen.route,
        arguments = listOf(
            navArgument("quizID") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val quizID: String = backStackEntry.savedStateHandle.get<String>("quizID") ?: ""
        QuizScreen(router,quizID)
    }
}
