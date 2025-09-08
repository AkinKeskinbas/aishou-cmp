package com.keak.aishou.purchase.model

import com.revenuecat.purchases.kmp.models.Package

data class RevenueCatPackage(
val packageId: String, // e.g., "monthly", "annual" (RevenueCat default ids)
val productId: String, // Store product id from App Store / Play
val title: String,
val description: String,
val priceFormatted: String, // e.g., "$4.99"
val periodLabel: String? = null, // e.g., "Monthly", "Annual" if detectable
val productPackage:Package

)