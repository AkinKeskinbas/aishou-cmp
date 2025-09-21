package com.keak.aishou.utils

import android.content.Context

actual object ShareHelperFactory {
    private lateinit var context: Context

    fun initialize(context: Context) {
        this.context = context.applicationContext
    }

    actual fun create(): ShareHelper {
        return ShareHelper(context)
    }
}