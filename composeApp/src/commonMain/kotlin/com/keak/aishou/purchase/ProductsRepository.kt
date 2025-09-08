package com.keak.aishou.purchase

import com.keak.aishou.purchase.model.RevenueCatPackage
import kotlinx.coroutines.flow.StateFlow

interface ProductsRepository {
val state: StateFlow<ProductsState>
/** Force refresh from RC (safe to call anytime). */
suspend fun getProducts()
/** Shortcut to get a specific package by its RevenueCat package identifier, e.g. "monthly" or "annual". */
fun findByPackageId(id: String): RevenueCatPackage?
}
sealed interface ProductsState {
    data object Loading : ProductsState
    data object Empty : ProductsState
    data class Loaded(val packages: List<RevenueCatPackage>) : ProductsState
    data class Error(val message: String) : ProductsState
}