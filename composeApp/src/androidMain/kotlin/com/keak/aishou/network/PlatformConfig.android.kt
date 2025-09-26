package com.keak.aishou.network

actual object PlatformConfig {
    actual val baseUrl: String = run {
        // Check if app is debuggable to determine environment
        val isDebug = try {
            Class.forName("com.keak.aishou.BuildConfig").getDeclaredField("DEBUG").getBoolean(null)
        } catch (e: Exception) {
            // Fallback: use a system property or assume production
            false
        }

        if (isDebug) {
            "http://10.0.2.2:3060" // Debug: Local development server for Android emulator
        } else {
            "https://api.aishou.site" // Release: Production API
        }
    }
}
