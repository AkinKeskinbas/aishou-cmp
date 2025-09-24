package com.keak.aishou.di

import com.keak.aishou.data.AppInitializationService
import com.keak.aishou.data.DataStoreFactory
import com.keak.aishou.data.DataStoreManager
import com.keak.aishou.data.UserSessionManager
import com.keak.aishou.data.UserRegistrationService
import com.keak.aishou.data.PersonalityDataManager
import com.keak.aishou.data.language.LanguageManager
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
import com.keak.aishou.screens.allresults.AllResultsViewModel
import com.keak.aishou.screens.allresults.TestResultViewModel
import com.keak.aishou.screens.homescreen.HomeViewModel
import com.keak.aishou.screens.quicktestscreen.QuickTestHomeScreenViewModel
import com.keak.aishou.screens.quicktestscreen.QuizViewModel
import com.keak.aishou.screens.splashscreen.SplashViewModel
import com.keak.aishou.screens.reauth.ReAuthViewModel
import com.keak.aishou.screens.friends.FriendsViewModel
import com.keak.aishou.screens.notifications.NotificationsViewModel
import com.keak.aishou.screens.invite.InviteViewModel
import com.keak.aishou.screens.profile.ProfileViewModel
import com.keak.aishou.notifications.OneSignalFactory
import com.keak.aishou.notifications.OneSignalService
import com.keak.aishou.utils.ShareHelperFactory
import com.keak.aishou.utils.ImageShareHelperFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

// Platform module - will be provided by platform-specific implementations
expect val platformModule: Module

val dataModules = module {
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Main) }


    single { DataStoreFactory.createDataStore() }
    single { DataStoreManager(get()) }
    single { UserSessionManager(get(), get()) }

    // Language Management - Platform-specific LanguageDetector will be provided by platform modules
    single { LanguageManager(get(), get(), get()) }

    // User Registration Service
    single { UserRegistrationService(get(), get(), get(), get(), get(), get()) }

    // App Initialization Service
    single { AppInitializationService(get(), get(), get(), get(), get()) }

    // Personality Data Manager
    single { PersonalityDataManager(get()) }

    single<AishouApiService> { AishouApiImpl(get(), get()) }
    single<PremiumRepository> { RevenueCatPremiumRepository() }
    single { PremiumPresenter(repo = get(), scope = get()) }
    single<ProductsRepository> { RevenueCatProductsRepository() }
    single { QuickTestHomeMapper() }
    single<QuicTestScreenRepository> { QuickTestRepositoryImpl(get(), get()) }

    // OneSignal
    single { OneSignalFactory.createOneSignalManager() }
    single { OneSignalService(get(), get(), get(), get(), get()) }

    // Share Helper
    single { ShareHelperFactory.create() }
    single { ImageShareHelperFactory.create() }


}

val domainModule = module {
    single { QuickTestHomeUseCase(get()) }
}
val viewModelModule = module {
    factory { AllResultsViewModel(get()) }
    factory { TestResultViewModel(get(), get()) }
    factory { HomeViewModel(get(), get(), get()) }
    viewModelOf(::QuickTestHomeScreenViewModel)
    factory { QuizViewModel(get(), get()) }
    factory { SplashViewModel(get(), get()) }
    factory { ReAuthViewModel(get(), get()) }
    factory { FriendsViewModel(get()) }
    factory { NotificationsViewModel(get()) }
    factory { InviteViewModel(get()) }
    factory { ProfileViewModel(get(), get()) }
}
