package com.keak.aishou.navigation

import platform.Foundation.NSLog

// Bridge object that can be called from Swift
object RouterBridgeIOS {

    private var router: Router? = null

    fun setRouter(router: Router) {
        NSLog("RouterBridge iOS: Router set")
        this.router = router
    }

    fun goToNotifications() {
        NSLog("RouterBridge iOS: Navigating to notifications")
        router?.goToNotifications() ?: NSLog("RouterBridge iOS: ⚠️ Router not set")
    }

    fun goToHome() {
        NSLog("RouterBridge iOS: Navigating to home")
        router?.goToHome() ?: NSLog("RouterBridge iOS: ⚠️ Router not set")
    }

    fun goToFriendRequest(senderId: String, senderName: String) {
        NSLog("RouterBridge iOS: Navigating to friend request - senderId: $senderId, senderName: $senderName")
        router?.goToFriendRequest(senderId, senderName) ?: NSLog("RouterBridge iOS: ⚠️ Router not set")
    }

    fun goToInvite(inviteId: String, senderId: String, testId: String, testTitle: String) {
        NSLog("RouterBridge iOS: Navigating to invite - inviteId: $inviteId")
        router?.goToInvite(inviteId, senderId, testId, testTitle) ?: NSLog("RouterBridge iOS: ⚠️ Router not set")
    }

    fun goToTestResult(testId: String) {
        NSLog("RouterBridge iOS: Navigating to test result - testId: $testId")
        router?.goToTestResultScreen(testId) ?: NSLog("RouterBridge iOS: ⚠️ Router not set")
    }
}