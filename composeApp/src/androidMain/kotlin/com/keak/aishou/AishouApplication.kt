package com.keak.aishou

import android.app.Application

class AishouApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: AishouApplication
            private set
    }
}