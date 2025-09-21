package com.keak.aishou.data.language

/**
 * Example usage of the Language Management System
 *
 * This file demonstrates how to use the LanguageManager
 * throughout your application.
 */

/*
// 1. Initialize in your Application/App startup
class MyApp {
    fun onCreate() {
        // LanguageManager is injected via Koin DI
        val languageManager = get<LanguageManager>()
        languageManager.initialize()
    }
}

// 2. Use in ViewModels
class MyViewModel(
    private val languageManager: LanguageManager
) : ViewModel() {

    // Observe current language
    val currentLanguage = languageManager.currentLanguage

    // Get language code for API calls
    suspend fun makeApiCall() {
        val languageCode = languageManager.getCurrentLanguageCode()
        apiService.getData(language = languageCode)
    }

    // Change language
    suspend fun changeLanguage(newLanguage: AppLanguage) {
        languageManager.setLanguage(newLanguage)
    }

    // Reset to system language
    suspend fun resetLanguage() {
        languageManager.resetToSystemLanguage()
    }
}

// 3. Use in Composables
@Composable
fun LanguageSelector(
    languageManager: LanguageManager = get()
) {
    val currentLanguage by languageManager.currentLanguage.collectAsState()
    val availableLanguages = remember { languageManager.getAvailableLanguages() }

    LazyColumn {
        items(availableLanguages) { language ->
            LanguageItem(
                language = language,
                isSelected = currentLanguage?.languageCode == language.languageCode,
                onClick = {
                    // Change language
                    scope.launch {
                        languageManager.setLanguage(language)
                    }
                }
            )
        }
    }
}

// 4. Check RTL support
@Composable
fun MyScreen(languageManager: LanguageManager = get()) {
    LaunchedEffect(Unit) {
        val isRTL = languageManager.isCurrentLanguageRTL()
        // Adjust layout for RTL languages
    }
}

// 5. Format strings based on locale
suspend fun formatCurrency(amount: Double, languageManager: LanguageManager): String {
    val locale = languageManager.getCurrentLocale()
    // Use locale for formatting
    return when (locale) {
        "tr_TR" -> "₺$amount"
        "en_US" -> "$${amount}"
        "es_ES" -> "${amount}€"
        else -> "$${amount}"
    }
}

// 6. API integration
class ApiService(private val languageManager: LanguageManager) {

    suspend fun fetchLocalizedContent(): Content {
        val languageCode = languageManager.getCurrentLanguageCode()
        return httpClient.get("/content") {
            parameter("lang", languageCode)
        }
    }
}

// 7. Settings Screen
@Composable
fun SettingsScreen(languageManager: LanguageManager = get()) {
    val currentLanguage by languageManager.currentLanguage.collectAsState()

    Column {
        Text("Current Language: ${currentLanguage?.displayName}")

        Button(
            onClick = {
                scope.launch {
                    languageManager.resetToSystemLanguage()
                }
            }
        ) {
            Text("Reset to System Language")
        }
    }
}
*/