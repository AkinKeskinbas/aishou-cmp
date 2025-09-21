package com.keak.aishou.utils

actual object ShareHelperFactory {
    actual fun create(): ShareHelper {
        return ShareHelper()
    }
}