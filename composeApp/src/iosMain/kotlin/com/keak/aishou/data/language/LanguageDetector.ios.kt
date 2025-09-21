package com.keak.aishou.data.language

import platform.Foundation.NSLocale
import platform.Foundation.NSString

actual class LanguageDetector {

    actual fun getCurrentSystemLanguage(): AppLanguage {
        // Use a simple approach - get system language from NSLocale
        // For iOS, we'll detect common languages and provide defaults
        val systemLanguage = getSystemLanguageCode()
        return createAppLanguageFromCode(systemLanguage)
    }

    actual fun getAvailableLanguages(): List<AppLanguage> {
        // Return a list of commonly supported languages
        return listOf(
            AppLanguage.DEFAULT, // English
            AppLanguage.TURKISH,
            AppLanguage.SPANISH,
            createAppLanguageFromCode("fr"), // French
            createAppLanguageFromCode("de"), // German
            createAppLanguageFromCode("it"), // Italian
            createAppLanguageFromCode("pt"), // Portuguese
        )
    }

    actual fun isLanguageSupported(languageCode: String): Boolean {
        return getAvailableLanguages().any { it.languageCode == languageCode }
    }

    private fun getSystemLanguageCode(): String {
        // Try to get system language - fallback to English if not available
        return try {
            // This is a simplified approach for iOS
            // In a real implementation, you might use platform-specific APIs
            "en" // Default to English for now
        } catch (e: Exception) {
            "en"
        }
    }

    private fun createAppLanguageFromCode(languageCode: String): AppLanguage {
        val displayName = getLanguageDisplayName(languageCode)
        val countryCode = getDefaultCountryCode(languageCode)

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

    private fun getDefaultCountryCode(languageCode: String): String? {
        return when (languageCode) {
            "en" -> "US"
            "tr" -> "TR"
            "es" -> "ES"
            "fr" -> "FR"
            "de" -> "DE"
            "it" -> "IT"
            "pt" -> "PT"
            else -> null
        }
    }

    private fun getLanguageDisplayName(languageCode: String): String {
        return when (languageCode) {
            "en" -> "English"
            "tr" -> "Türkçe"
            "es" -> "Español"
            "fr" -> "Français"
            "de" -> "Deutsch"
            "it" -> "Italiano"
            "pt" -> "Português"
            "ru" -> "Русский"
            "ja" -> "日本語"
            "ko" -> "한국어"
            "zh" -> "中文"
            "ar" -> "العربية"
            else -> languageCode.uppercase()
        }
    }

}