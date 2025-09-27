package com.keak.aishou.network

import android.content.pm.ApplicationInfo

actual object PlatformConfig {
    actual val baseUrl: String = run {
        // Get application context to check if app is debuggable
        val context = try {
            Class.forName("android.app.ActivityThread")
                .getMethod("currentApplication")
                .invoke(null) as android.app.Application
        } catch (e: Exception) {
            null
        }

        val isDebug = context?.let { app ->
            (app.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } ?: false

        val selectedUrl = if (isDebug) {
            "http://172.22.1.83:3060" // Debug: Local development server for Android emulator
        } else {
            "https://api.aishou.site" // Release: Production API
        }

        println("PlatformConfig: isDebug=$isDebug, selectedUrl=$selectedUrl")
        selectedUrl
    }
}
