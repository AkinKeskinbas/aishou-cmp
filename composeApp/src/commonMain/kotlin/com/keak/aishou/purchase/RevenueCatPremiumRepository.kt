package com.keak.aishou.purchase

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.ktx.awaitCustomerInfo
import com.revenuecat.purchases.kmp.models.CacheFetchPolicy
import com.revenuecat.purchases.kmp.models.Package
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class RevenueCatPremiumRepository(
    private val entitlementId: String = Entitlements.PREMIUM,
    private val purchases: Purchases = Purchases.sharedInstance,
    private val io: CoroutineDispatcher = Dispatchers.Default
) : PremiumRepository {


    private val _state = MutableStateFlow<PremiumState>(PremiumState.Loading)
    override val state: StateFlow<PremiumState> = _state.asStateFlow()


    override suspend fun refresh(fetchPolicy: CacheFetchPolicy) = withContext(io) {
        try {
            val info = purchases.awaitCustomerInfo(fetchPolicy)
            val isPremium = info.entitlements.all[entitlementId]?.isActive == true
            _state.value = if (isPremium) PremiumState.Premium else PremiumState.Free
        } catch (t: Throwable) {
            _state.value = PremiumState.Error(t.message ?: "Unknown error")
        }
    }

    override suspend fun purchaseItem(
        product: Package,
        onSuccessEvent: (Package) -> Unit,
        onErrorEvent: (String, Boolean) -> Unit
    ) {
        val info = purchases.awaitCustomerInfo(CacheFetchPolicy.CACHED_OR_FETCHED)
        val isPremium = info.entitlements.all[entitlementId]?.isActive == true
        purchases.purchase(
            packageToPurchase = product,
            onSuccess = { storeTransaction, customerInfo ->
               if (isPremium.not()){
                    onSuccessEvent.invoke(product)
               }
            },
            onError = { error, userCancelled ->
                onErrorEvent.invoke(error.message, userCancelled)
            }

        )
    }
}