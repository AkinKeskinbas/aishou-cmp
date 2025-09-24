package com.keak.aishou.data.language

import platform.Foundation.NSUserDefaults

actual class LanguageDetector {

    actual fun getCurrentSystemLanguage(): AppLanguage {
        val identifier = getPreferredLocaleIdentifier()
        return createAppLanguageFromIdentifier(identifier)
    }

    actual fun getAvailableLanguages(): List<AppLanguage> {
        val preferredLanguages = getPreferredLanguageIdentifiers()
            .map(::createAppLanguageFromIdentifier)

        if (preferredLanguages.isNotEmpty()) {
            return preferredLanguages.distinctBy { it.languageCode }
        }

        // Fallback to common languages when preferred list is unavailable
        return listOf(
            AppLanguage.DEFAULT,
            AppLanguage.TURKISH,
            AppLanguage.SPANISH,
            createAppLanguageFromIdentifier("fr_FR"),
            createAppLanguageFromIdentifier("de_DE"),
            createAppLanguageFromIdentifier("it_IT"),
            createAppLanguageFromIdentifier("pt_PT"),
        )
    }

    actual fun isLanguageSupported(languageCode: String): Boolean {
        return getAvailableLanguages().any { it.languageCode == languageCode }
    }

    private fun getPreferredLocaleIdentifier(): String {
        return getPreferredLanguageIdentifiers().firstOrNull()
            ?: AppLanguage.DEFAULT.locale
    }

    private fun createAppLanguageFromIdentifier(identifier: String): AppLanguage {
        val normalized = identifier.replace('-', '_')
        val parts = normalized.split('_')
        val languageCode = parts.firstOrNull()?.lowercase() ?: AppLanguage.DEFAULT.languageCode

        val countryCode = parts
            .asSequence()
            .drop(1)
            .firstOrNull { part ->
                part.length == 2 && part.all { it.isLetter() } ||
                    part.length == 3 && part.all { it.isDigit() }
            }
            ?.uppercase()
            ?: getDefaultCountryCode(languageCode)

        val displayName = getLanguageDisplayName(languageCode)
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

    private fun getPreferredLanguageIdentifiers(): List<String> {
        val languages = NSUserDefaults.standardUserDefaults.arrayForKey("AppleLanguages")
        return (languages as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
    }

}
