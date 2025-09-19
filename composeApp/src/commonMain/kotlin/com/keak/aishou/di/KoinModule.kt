package com.keak.aishou.di

import com.keak.aishou.data.DataStoreFactory
import com.keak.aishou.data.DataStoreManager
import com.keak.aishou.data.UserSessionManager
import com.keak.aishou.domain.mapper.QuickTestHomeMapper
import com.keak.aishou.domain.repository.QuicTestScreenRepository
import com.keak.aishou.domain.repositoryimpl.QuickTestRepositoryImpl
import com.keak.aishou.domain.usecase.QuickTestHomeUseCase
import com.keak.aishou.network.AishouApiImpl
import com.keak.aishou.network.AishouApiService
import com.keak.aishou.purchase.PremiumPresenter
import com.keak.aishou.purchase.PremiumRepository
import com.keak.aishou.purchase.ProductsRepository
import com.keak.aishou.purchase.RevenueCatPremiumRepository
import com.keak.aishou.purchase.RevenueCatProductsRepository
import com.keak.aishou.screens.homescreen.HomeViewModel
import com.keak.aishou.screens.quicktestscreen.QuickTestHomeScreenViewModel
import com.keak.aishou.screens.splashscreen.SplashViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dataModules = module {
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Main) }

    single { DataStoreFactory.createDataStore() }
    single { DataStoreManager(get()) }
    single { UserSessionManager.getInstance(get(), get()) }

    single<AishouApiService> { AishouApiImpl() }
    single<PremiumRepository> { RevenueCatPremiumRepository() }
    single { PremiumPresenter(repo = get(), scope = get()) }
    single<ProductsRepository> { RevenueCatProductsRepository() }
    single { QuickTestHomeMapper() }
    single<QuicTestScreenRepository> { QuickTestRepositoryImpl(get()) }

}

val domainModule = module {
    single { QuickTestHomeUseCase(get()) }
}
val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::QuickTestHomeScreenViewModel)
    viewModelOf(::SplashViewModel)
}
