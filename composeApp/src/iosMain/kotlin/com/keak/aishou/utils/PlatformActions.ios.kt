package com.keak.aishou.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.Foundation.NSBundle

actual object PlatformActions {
    actual fun openStore() {
        // Get bundle ID for App Store URL
        val bundleId = NSBundle.mainBundle.bundleIdentifier ?: "com.keak.aishou"
        val appStoreUrl = "https://apps.apple.com/app/id/YOUR_APP_ID" // Replace with actual App Store ID

        NSURL.URLWithString(appStoreUrl)?.let { url ->
            if (UIApplication.sharedApplication.canOpenURL(url)) {
                UIApplication.sharedApplication.openURL(url, options = emptyMap<Any?, Any>()) { success ->
                    if (!success) {
                        println("Could not open App Store")
                    }
                }
            }
        }
    }

    actual fun sendEmail(to: String, subject: String, body: String) {
        val encodedSubject = subject.replace(" ", "%20")
        val encodedBody = body.replace(" ", "%20").replace("\n", "%0A")
        val mailtoUrl = "mailto:$to?subject=$encodedSubject&body=$encodedBody"

        NSURL.URLWithString(mailtoUrl)?.let { url ->
            if (UIApplication.sharedApplication.canOpenURL(url)) {
                UIApplication.sharedApplication.openURL(url, options = emptyMap<Any?, Any>()) { success ->
                    if (!success) {
                        println("Could not open mail app")
                    }
                }
            }
        }
    }

    actual fun openUrl(url: String) {
        NSURL.URLWithString(url)?.let { nsurl ->
            if (UIApplication.sharedApplication.canOpenURL(nsurl)) {
                UIApplication.sharedApplication.openURL(nsurl, options = emptyMap<Any?, Any>()) { success ->
                    if (!success) {
                        println("Could not open URL: $url")
                    }
                }
            }
        }
    }
}