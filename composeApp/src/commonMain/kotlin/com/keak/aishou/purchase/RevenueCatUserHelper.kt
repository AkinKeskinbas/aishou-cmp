package com.keak.aishou.purchase

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.ktx.awaitCustomerInfo

/**
 * Helper class to get RevenueCat user ID
 */
object RevenueCatUserHelper {

    /**
     * Get the RevenueCat app user ID
     */
    suspend fun getRevenueCatUserId(): String? {
        return try {
            val customerInfo = Purchases.sharedInstance.awaitCustomerInfo()
            val userId = customerInfo.originalAppUserId

            // Additional null/empty check for iOS safety
            val safeUserId = userId?.takeIf { it.isNotBlank() }

            println("RevenueCatUserHelper: Retrieved user ID: $safeUserId")
            safeUserId
        } catch (e: Exception) {
            println("RevenueCatUserHelper: Error getting user ID: ${e.message}")
            null
        }
    }


    /**
     * Check if RevenueCat is properly initialized and has a user ID
     */
    suspend fun isRevenueCatReady(): Boolean {
        return try {
            val userId = getRevenueCatUserId()
            userId != null && userId.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
}