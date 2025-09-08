package com.keak.aishou.navigation

sealed class Routes(val route:String = ""){
    data object Splash:Routes("Splash")
    data object Home:Routes("Home")
    data object Paywall:Routes("Paywall")
}