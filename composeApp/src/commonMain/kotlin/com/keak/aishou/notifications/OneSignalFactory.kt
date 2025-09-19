package com.keak.aishou.notifications

expect object OneSignalFactory {
    fun createOneSignalManager(): OneSignalManager
}