package com.keak.aishou.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity

actual object PlatformActions {
    private var appContext: Context? = null

    fun initialize(context: Context) {
        appContext = context
    }

    actual fun openStore() {
        val context = appContext ?: return

        try {
            // Try to open Play Store app first
            val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(playStoreIntent)
        } catch (e: ActivityNotFoundException) {
            // Fallback to Play Store website
            val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(browserIntent)
            } catch (e: Exception) {
                println("Could not open Play Store: ${e.message}")
            }
        }
    }

    actual fun sendEmail(to: String, subject: String, body: String) {
        val context = appContext ?: return

        try {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$to")
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooserIntent = Intent.createChooser(emailIntent, "Send email via...")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            println("Could not send email: ${e.message}")
        }
    }

    actual fun openUrl(url: String) {
        val context = appContext ?: return

        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
        } catch (e: Exception) {
            println("Could not open URL: ${e.message}")
        }
    }
}