package com.keak.aishou.utils

import platform.UIKit.*
import platform.Foundation.*
import kotlinx.cinterop.*

actual class ShareHelper {

    actual suspend fun shareText(text: String, title: String) {
        val activityViewController = UIActivityViewController(
            activityItems = listOf(NSString.create(string = text)),
            applicationActivities = null
        )

        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(
            activityViewController,
            animated = true,
            completion = null
        )
    }

    actual suspend fun copyToClipboard(text: String, label: String) {
        UIPasteboard.generalPasteboard.string = text
    }

    actual suspend fun shareToInstagram(text: String) {
        val instagramUrl = "instagram://app"
        val url = NSURL.URLWithString(instagramUrl)

        if (UIApplication.sharedApplication.canOpenURL(url!!)) {
            // Instagram is available, try to share
            shareText(text, "Share to Instagram")
        } else {
            // Instagram not available, fallback to regular share
            shareText(text, "Share to Instagram")
        }
    }

    actual suspend fun shareToLine(text: String) {
        val lineUrl = "line://app"
        val url = NSURL.URLWithString(lineUrl)

        if (UIApplication.sharedApplication.canOpenURL(url!!)) {
            // LINE is available, try to share
            shareText(text, "Share to LINE")
        } else {
            // LINE not available, fallback to regular share
            shareText(text, "Share to LINE")
        }
    }
}