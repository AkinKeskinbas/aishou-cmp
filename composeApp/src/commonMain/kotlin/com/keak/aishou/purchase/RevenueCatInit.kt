package com.keak.aishou.purchase

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.configure

object RevenueCatInit {
    /** Her platformda bir kez çağrılmalı */
    fun configure(apiKey: String, appUserId: String? = null) {
        Purchases.configure(apiKey) {
            appUserId?.let { appUserId(it) }
        }
    }
}