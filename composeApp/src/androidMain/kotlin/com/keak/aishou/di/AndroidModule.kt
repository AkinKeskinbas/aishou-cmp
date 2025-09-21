package com.keak.aishou.di

import android.content.Context
import com.keak.aishou.data.language.LanguageDetector
import org.koin.dsl.module

val androidModule = module {
    // Context will be provided by androidContext() in Application
    single { LanguageDetector(get<Context>()) }
}