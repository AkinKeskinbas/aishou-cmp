package com.keak.aishou.data.language

/**
 * Represents app language information
 */
data class AppLanguage(
    val languageCode: String,  // e.g., "en", "tr", "es"
    val countryCode: String?,  // e.g., "US", "TR", "ES"
    val displayName: String,   // e.g., "English", "Türkçe", "Español"
    val locale: String         // e.g., "en_US", "tr_TR", "es_ES"
) {
    companion object {
        val DEFAULT = AppLanguage(
            languageCode = "en",
            countryCode = "US",
            displayName = "English",
            locale = "en_US"
        )

        val TURKISH = AppLanguage(
            languageCode = "tr",
            countryCode = "TR",
            displayName = "Türkçe",
            locale = "tr_TR"
        )

        val SPANISH = AppLanguage(
            languageCode = "es",
            countryCode = "ES",
            displayName = "Español",
            locale = "es_ES"
        )
    }
}