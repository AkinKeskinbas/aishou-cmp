package com.keak.aishou.di

import com.keak.aishou.data.language.LanguageDetector
import org.koin.dsl.module

val iosModule = module {
    single { LanguageDetector() }
}