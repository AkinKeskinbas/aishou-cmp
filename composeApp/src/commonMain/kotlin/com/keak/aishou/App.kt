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
import com.keak.aishou.di.platformModule
import com.keak.aishou.navigation.AishouNavGraph
import com.keak.aishou.navigation.Router
import com.keak.aishou.navigation.RouterBridge
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
fun App(deepLinkUrl: String? = null) {
    KoinMultiplatformApplication(
        config = KoinConfiguration {
            modules(platformModule, viewModelModule, dataModules, domainModule)
        }
    ) {
        val navController: NavHostController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val route = navBackStackEntry?.destination?.route ?: Routes.Splash.route
        val router: Router = remember { RouterImpl(navController, route) }

        // Set router for iOS bridge
        LaunchedEffect(router) {
            RouterBridge.setRouter(router)
        }
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

        // Handle deeplink navigation
        LaunchedEffect(deepLinkUrl) {
            deepLinkUrl?.let { url ->
                println("App: Processing deeplink: $url")
                handleDeepLinkNavigation(url, router)
            }
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

private fun handleDeepLinkNavigation(url: String, router: Router) {
    try {
        println("App: Parsing deeplink URL: $url")

        // Parse the URL
        // Example: https://aishou.site/friends/request?senderId=xxx&senderName=John%20Doe
        // or: aishou://friends/request?senderId=xxx&senderName=John%20Doe
        if (url.contains("/friends/request") || url.contains("friends/request")) {
            // Extract parameters
            val senderId = extractUrlParameter(url, "senderId")
            val senderName = extractUrlParameter(url, "senderName")

            if (senderId != null && senderName != null) {
                println("App: Navigating to friend request with senderId=$senderId, senderName=$senderName")
                router.goToFriendRequest(senderId, senderName)
            } else {
                println("App: Invalid friend request deeplink - missing parameters")
                router.goToNotifications() // Fallback to notifications
            }
        } else if (url.contains("/invite/")) {
            // Handle invite deeplinks
            // Example: https://aishou.site/invite/123?senderId=xxx&testId=yyy&testTitle=Test
            val inviteId = extractPathParameter(url, "/invite/")
            val senderId = extractUrlParameter(url, "senderId")
            val testId = extractUrlParameter(url, "testId")
            val testTitle = extractUrlParameter(url, "testTitle") ?: "Test"

            if (inviteId != null && senderId != null && testId != null) {
                println("App: Navigating to invite with inviteId=$inviteId")
                router.goToInvite(inviteId, senderId, testId, testTitle)
            } else {
                println("App: Invalid invite deeplink - missing parameters")
                router.goToHome() // Fallback to home
            }
        } else {
            println("App: Unknown deeplink pattern, navigating to home")
            router.goToHome()
        }
    } catch (e: Exception) {
        println("App: Error parsing deeplink: ${e.message}")
        router.goToHome() // Fallback to home on error
    }
}

private fun extractUrlParameter(url: String, parameter: String): String? {
    return try {
        val regex = "$parameter=([^&]+)".toRegex()
        val matchResult = regex.find(url)
        matchResult?.groupValues?.get(1)?.let { value ->
            // URL decode
            value.replace("%20", " ").replace("%26", "&")
        }
    } catch (e: Exception) {
        null
    }
}

private fun extractPathParameter(url: String, basePath: String): String? {
    return try {
        val startIndex = url.indexOf(basePath) + basePath.length
        val endIndex = url.indexOf("?", startIndex).takeIf { it != -1 } ?: url.length
        if (startIndex < endIndex) {
            url.substring(startIndex, endIndex)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}