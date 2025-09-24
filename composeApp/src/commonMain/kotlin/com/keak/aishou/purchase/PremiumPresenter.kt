package com.keak.aishou.purchase

import com.revenuecat.purchases.kmp.models.CacheFetchPolicy
import com.revenuecat.purchases.kmp.models.Package
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PremiumPresenter(
    private val repo: PremiumRepository,
    private val scope: CoroutineScope
) {
    val viewState: StateFlow<PremiumState> = repo.state
    fun onAppStart() = scope.launch { repo.refresh(CacheFetchPolicy.FETCH_CURRENT) }
    fun onRestoreOrPurchase() = scope.launch { repo.refresh(CacheFetchPolicy.FETCH_CURRENT) }
    fun softRefresh() = scope.launch { repo.refresh() }
    fun restorePuchases() = scope.launch { repo.restore()
    repo.refresh(CacheFetchPolicy.FETCH_CURRENT)}
    fun buyProduct(
        product: Package,
        onSuccessEvent: (product: Package) -> Unit,
        onErrorEvent: (error: String, userCancelled: Boolean) -> Unit
    ) = scope.launch { repo.purchaseItem(product, onSuccessEvent, onErrorEvent) }
}