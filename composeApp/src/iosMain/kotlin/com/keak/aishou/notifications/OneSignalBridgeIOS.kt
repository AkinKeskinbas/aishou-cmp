package com.keak.aishou.notifications

import platform.Foundation.NSLog

// Bridge object that can be called from Swift
object OneSignalBridgeIOS {

    private var cachedPushSubscriptionId: String? = null
    private var cachedOneSignalId: String? = null

    // These functions will be called from Swift/Objective-C
    fun setPushSubscriptionId(id: String?) {
        NSLog("OneSignal iOS: Bridge received pushSubscriptionId: $id")
        cachedPushSubscriptionId = id
    }

    fun setOneSignalId(id: String?) {
        NSLog("OneSignal iOS: Bridge received onesignalId: $id")
        cachedOneSignalId = id
    }

    fun getPushSubscriptionId(): String? {
        NSLog("OneSignal iOS: Bridge returning pushSubscriptionId: $cachedPushSubscriptionId")
        return cachedPushSubscriptionId
    }

    fun getOneSignalId(): String? {
        NSLog("OneSignal iOS: Bridge returning onesignalId: $cachedOneSignalId")
        return cachedOneSignalId
    }
}