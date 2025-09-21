package com.keak.aishou

import com.keak.aishou.ui.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.keak.aishou.data.AppInitializationService
import com.keak.aishou.data.UserSessionManager
import com.keak.aishou.di.dataModules
import com.keak.aishou.di.domainModule
import com.keak.aishou.di.viewModelModule
import com.keak.aishou.navigation.AishouNavGraph
import com.keak.aishou.navigation.Router
import com.keak.aishou.navigation.RouterImpl
import com.keak.aishou.navigation.Routes
import com.keak.aishou.purchase.PlatformKeys
import com.keak.aishou.purchase.PremiumChecker
import com.keak.aishou.purchase.PremiumPresenter
import com.keak.aishou.purchase.PremiumState
import com.keak.aishou.purchase.RevenueCatInit
import org.koin.compose.KoinMultiplatformApplication
import org.koin.compose.koinInject
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration

@OptIn(KoinExperimentalAPI::class)
@Composable
fun App() {
    KoinMultiplatformApplication(
        config = KoinConfiguration {
            modules(viewModelModule, dataModules, domainModule)
        }
    ) {
        val navController: NavHostController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val route = navBackStackEntry?.destination?.route ?: Routes.Splash.route
        val router: Router = remember { RouterImpl(navController, route) }
        RevenueCatInit.configure(apiKey = PlatformKeys.revenuecatApiKey)
        val presenter: PremiumPresenter = koinInject()
        val userSessionManager: UserSessionManager = koinInject()
        val appInitService: AppInitializationService = koinInject()

        LaunchedEffect(Unit) {
            // Initialize all app services (includes user registration)
            appInitService.initialize()

            // Keep existing premium initialization
            presenter.onAppStart()
        }

        PremiumChecker.stateProvider = { presenter.viewState.value }


        CompositionLocalProvider(

        ){
            MaterialTheme {
                AishouNavGraph(
                    navController = navController,
                    router = router,
                    modifier = Modifier.safeDrawingPadding()
                )
            }
        }

    }
}