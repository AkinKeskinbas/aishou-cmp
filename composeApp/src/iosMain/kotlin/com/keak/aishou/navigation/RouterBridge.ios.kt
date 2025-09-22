package com.keak.aishou.navigation

// iOS implementation - delegates to RouterBridgeIOS
actual object RouterBridge {
    actual fun setRouter(router: Router) {
        RouterBridgeIOS.setRouter(router)
    }
}