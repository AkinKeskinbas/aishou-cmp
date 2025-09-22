package com.keak.aishou.navigation

// Android implementation - no-op
actual object RouterBridge {
    actual fun setRouter(router: Router) {
        // No need for bridge on Android
    }
}