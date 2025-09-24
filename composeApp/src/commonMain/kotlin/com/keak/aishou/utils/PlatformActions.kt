package com.keak.aishou.utils

expect object PlatformActions {
    fun openStore()
    fun sendEmail(to: String, subject: String = "", body: String = "")
    fun openUrl(url: String)
}