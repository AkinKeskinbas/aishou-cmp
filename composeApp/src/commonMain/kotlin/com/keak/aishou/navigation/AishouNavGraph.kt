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
import com.keak.aishou.screens.allresults.TestResultScreen
import com.keak.aishou.screens.paywall.PaywallScreen
import com.keak.aishou.screens.homescreen.HomeScreen
import com.keak.aishou.screens.homescreen.HomeViewModel
import com.keak.aishou.screens.splashscreen.SplashScreen
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
        SplashScreen(router = router)
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
        route = Routes.TestResult.route,
        arguments = listOf(
            navArgument("testID") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val testID: String = backStackEntry.savedStateHandle.get<String>("testID") ?: ""
        TestResultScreen(router = router, testID = testID)
    }
}
