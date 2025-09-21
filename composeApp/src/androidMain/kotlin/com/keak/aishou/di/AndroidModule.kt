package com.keak.aishou.di

import android.content.Context
import com.keak.aishou.data.language.LanguageDetector
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    // Context will be provided by androidContext() in Application
    single { LanguageDetector(get<Context>()) }
}

val androidModule = module {
    // Context will be provided by androidContext() in Application
    single { LanguageDetector(get<Context>()) }
}