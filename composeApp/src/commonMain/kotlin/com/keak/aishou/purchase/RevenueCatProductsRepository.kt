package com.keak.aishou.purchase

import com.keak.aishou.purchase.model.RevenueCatPackage
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.ktx.awaitOfferings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class RevenueCatProductsRepository(
    private val purchases: Purchases = Purchases.sharedInstance,
    private val io: CoroutineDispatcher = Dispatchers.Default
) : ProductsRepository {

    private val _state = MutableStateFlow<ProductsState>(ProductsState.Loading)
    override val state: StateFlow<ProductsState> = _state.asStateFlow()


    private var cache: List<RevenueCatPackage> = emptyList()


    override suspend fun getProducts() = withContext(io) {
        try {
            println("AKN----PAYWALL--->Geting Products...")
            purchases.getOfferings(
                onError = {

                },
                onSuccess = { offerings ->
                    val combinedPackages = offerings.all.values.flatMap { offer ->
                        offer.availablePackages.map { pkg ->
                            println("AKN-PurchasePrice-->${pkg.storeProduct.price.formatted}")
                            println("AKN-PurchaseId-->${pkg.storeProduct.id}")
                            println("AKN-PurchasePeriodLabel-->${pkg.storeProduct.period?.unit?.name}")
                            println("AKN-PurchaseDesc-->${pkg.storeProduct.localizedDescription}")
                            println("AKN-PurchaseIdentifier-->${pkg.identifier}")
                            println("AKN-PurchaseTitle-->${pkg.packageType.name}")
                            RevenueCatPackage(
                                productId = pkg.storeProduct.id,
                                packageId = pkg.identifier,
                                periodLabel = pkg.storeProduct.period?.unit?.name,
                                title = pkg.packageType.name,
                                description = pkg.storeProduct.localizedDescription.orEmpty(),
                                priceFormatted = pkg.storeProduct.price.formatted,
                                productPackage = pkg
                            )
                        }
                    }
                    _state.value =
                        if (combinedPackages.isEmpty()) ProductsState.Empty else ProductsState.Loaded(
                            combinedPackages
                        )
                }
            )

        } catch (t: Throwable) {
            println("AKN----PAYWALL--->ERROR ${t.message}")
            _state.value = ProductsState.Error(t.message ?: "Failed to load products")
        }
    }


    override fun findByPackageId(id: String): RevenueCatPackage? =
        cache.firstOrNull { it.packageId == id }
}