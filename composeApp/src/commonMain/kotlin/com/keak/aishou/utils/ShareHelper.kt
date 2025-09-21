package com.keak.aishou.utils

expect class ShareHelper {
    suspend fun shareText(text: String, title: String = "Share")
    suspend fun copyToClipboard(text: String, label: String = "Copied")
    suspend fun shareToInstagram(text: String)
    suspend fun shareToLine(text: String)
}