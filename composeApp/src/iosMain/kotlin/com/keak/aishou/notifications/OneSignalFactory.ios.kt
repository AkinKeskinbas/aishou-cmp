package com.keak.aishou.notifications

actual object OneSignalFactory {
    actual fun createOneSignalManager(): OneSignalManager {
        return OneSignalManagerIOS()
    }
}