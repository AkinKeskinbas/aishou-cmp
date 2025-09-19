package com.keak.aishou.notifications

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual object OneSignalFactory : KoinComponent {
    actual fun createOneSignalManager(): OneSignalManager {
        val context: Context by inject()
        return OneSignalManagerAndroid(context)
    }
}