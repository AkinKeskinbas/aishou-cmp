package com.keak.aishou

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform