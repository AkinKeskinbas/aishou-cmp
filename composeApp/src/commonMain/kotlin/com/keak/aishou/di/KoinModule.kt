package com.keak.aishou.di

import com.keak.aishou.network.AishouApiImpl
import com.keak.aishou.network.AishouApiService
import com.keak.aishou.purchase.PremiumPresenter
import com.keak.aishou.purchase.PremiumRepository
import com.keak.aishou.purchase.ProductsRepository
import com.keak.aishou.purchase.RevenueCatPremiumRepository
import com.keak.aishou.purchase.RevenueCatProductsRepository
import com.keak.aishou.screens.homescreen.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dataModules = module {
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Main) }

    single<AishouApiService> { AishouApiImpl() }
    single<PremiumRepository> { RevenueCatPremiumRepository() }
    single { PremiumPresenter(repo = get(), scope = get()) }
    single<ProductsRepository> { RevenueCatProductsRepository() }

}


val viewModelModule = module {
    viewModelOf(::HomeViewModel) // Koin 4.x DSL
}
