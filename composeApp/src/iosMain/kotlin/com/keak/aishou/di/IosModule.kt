package com.keak.aishou.di

import com.keak.aishou.data.language.LanguageDetector
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { LanguageDetector() }
}

val iosModule = module {
    single { LanguageDetector() }
}