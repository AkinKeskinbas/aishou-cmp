package com.keak.aishou.navigation

import platform.Foundation.NSLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

// Bridge object that can be called from Swift
object RouterBridgeIOS {

    private var router: Router? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

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

    fun goToInvite(inviteId: String, senderId: String, testId: String, testTitle: String, senderName: String = "Unknown User", senderMbti: String? = null) {
        NSLog("RouterBridge iOS: Navigating to invite - inviteId: $inviteId, senderName: $senderName")
        router?.goToInvite(inviteId, senderId, testId, testTitle, senderName, senderMbti) ?: NSLog("RouterBridge iOS: ⚠️ Router not set")
    }

    fun goToTestResult(testId: String) {
        NSLog("RouterBridge iOS: Navigating to test result - testId: $testId")
        router?.goToTestResultScreen(testId) ?: NSLog("RouterBridge iOS: ⚠️ Router not set")
    }

    // New method to handle deep links through DeepLinkCoordinator
    fun handleDeepLink(url: String) {
        NSLog("RouterBridge iOS: Handling deep link through coordinator: $url")
        coroutineScope.launch {
            try {
                DeepLinkCoordinator.emit(url)
                NSLog("RouterBridge iOS: Deep link emitted successfully")
            } catch (e: Exception) {
                NSLog("RouterBridge iOS: Error emitting deep link: ${e.message}")
            }
        }
    }
}