package com.keak.aishou.data.language

import android.content.Context
import android.os.Build
import java.util.Locale

actual class LanguageDetector(private val context: Context) {

    actual fun getCurrentSystemLanguage(): AppLanguage {
        val locale = getCurrentLocale()
        return createAppLanguageFromLocale(locale)
    }

    actual fun getAvailableLanguages(): List<AppLanguage> {
        val availableLocales = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.let { locales ->
                (0 until locales.size()).map { locales[it] }
            }
        } else {
            @Suppress("DEPRECATION")
            listOf(context.resources.configuration.locale)
        }

        return availableLocales.map { createAppLanguageFromLocale(it) }.distinctBy { it.languageCode }
    }

    actual fun isLanguageSupported(languageCode: String): Boolean {
        return getAvailableLanguages().any { it.languageCode == languageCode }
    }

    private fun getCurrentLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
    }

    private fun createAppLanguageFromLocale(locale: Locale): AppLanguage {
        val languageCode = locale.language
        val countryCode = locale.country.takeIf { it.isNotEmpty() }
        val displayName = locale.getDisplayLanguage(locale).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
        }
        val localeString = if (countryCode != null) {
            "${languageCode}_${countryCode}"
        } else {
            languageCode
        }

        return AppLanguage(
            languageCode = languageCode,
            countryCode = countryCode,
            displayName = displayName,
            locale = localeString
        )
    }
}