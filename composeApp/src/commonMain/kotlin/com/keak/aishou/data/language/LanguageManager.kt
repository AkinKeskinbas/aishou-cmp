package com.keak.aishou.data.language

import com.keak.aishou.data.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Centralized language management service
 * Handles language detection, storage, and retrieval
 */
class LanguageManager(
    private val languageDetector: LanguageDetector,
    private val dataStoreManager: DataStoreManager,
    private val scope: CoroutineScope
) {

    /**
     * Current app language as Flow
     */
    val currentLanguage: Flow<AppLanguage?> = dataStoreManager.appLanguage

    /**
     * Initialize language system - should be called on app start
     */
    fun initialize() {
        scope.launch {
            // Check if we have a stored language
            val storedLanguage = dataStoreManager.appLanguage.first()

            if (storedLanguage == null) {
                // First time - detect and store system language
                val systemLanguage = languageDetector.getCurrentSystemLanguage()
                dataStoreManager.setAppLanguage(systemLanguage)

                println("LanguageManager: First time initialization - detected system language: ${systemLanguage.displayName} (${systemLanguage.locale})")
            } else {
                println("LanguageManager: Using stored language: ${storedLanguage.displayName} (${storedLanguage.locale})")
            }
        }
    }

    /**
     * Get current language synchronously (use with caution - prefer Flow)
     */
    suspend fun getCurrentLanguageSync(): AppLanguage {
        return currentLanguage.first() ?: languageDetector.getCurrentSystemLanguage()
    }

    /**
     * Set app language manually
     */
    suspend fun setLanguage(language: AppLanguage) {
        dataStoreManager.setAppLanguage(language)
        println("LanguageManager: Language changed to: ${language.displayName} (${language.locale})")
    }

    /**
     * Reset to system language
     */
    suspend fun resetToSystemLanguage() {
        val systemLanguage = languageDetector.getCurrentSystemLanguage()
        dataStoreManager.setAppLanguage(systemLanguage)
        println("LanguageManager: Reset to system language: ${systemLanguage.displayName} (${systemLanguage.locale})")
    }

    /**
     * Get all available languages on the device
     */
    fun getAvailableLanguages(): List<AppLanguage> {
        return languageDetector.getAvailableLanguages()
    }

    /**
     * Check if a language is supported
     */
    fun isLanguageSupported(languageCode: String): Boolean {
        return languageDetector.isLanguageSupported(languageCode)
    }

    /**
     * Get language code only (useful for API calls)
     */
    suspend fun getCurrentLanguageCode(): String {
        return getCurrentLanguageSync().languageCode
    }

    /**
     * Get locale string (useful for formatting)
     */
    suspend fun getCurrentLocale(): String {
        return getCurrentLanguageSync().locale
    }

    /**
     * Check if current language is RTL (Right-to-Left)
     */
    suspend fun isCurrentLanguageRTL(): Boolean {
        val languageCode = getCurrentLanguageCode()
        return RTL_LANGUAGES.contains(languageCode)
    }

    companion object {
        // Common RTL language codes
        private val RTL_LANGUAGES = setOf("ar", "fa", "he", "ur", "yi")
    }
}