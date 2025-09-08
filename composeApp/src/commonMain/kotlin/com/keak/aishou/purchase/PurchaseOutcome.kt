package com.keak.aishou.purchase

import com.revenuecat.purchases.kmp.models.CustomerInfo

sealed interface PurchaseOutcome {
    data class Success(val info: CustomerInfo) : PurchaseOutcome
    data object Cancelled : PurchaseOutcome
    data class Error(val message: String) : PurchaseOutcome
}