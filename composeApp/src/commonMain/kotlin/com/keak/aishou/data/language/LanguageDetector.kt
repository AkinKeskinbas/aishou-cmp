package com.keak.aishou.data.language

/**
 * Platform-specific language detection interface
 */
expect class LanguageDetector {
    /**
     * Get the current system language
     */
    fun getCurrentSystemLanguage(): AppLanguage

    /**
     * Get all available system languages
     */
    fun getAvailableLanguages(): List<AppLanguage>

    /**
     * Check if a specific language is supported by the system
     */
    fun isLanguageSupported(languageCode: String): Boolean
}