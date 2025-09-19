package com.keak.aishou

import android.app.Application
import com.keak.aishou.di.androidModule
import com.keak.aishou.di.dataModules
import com.keak.aishou.di.domainModule
import com.keak.aishou.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AishouApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@AishouApplication)
            modules(
                androidModule,
                dataModules,
                domainModule,
                viewModelModule
            )
        }
    }

    companion object {
        lateinit var instance: AishouApplication
            private set
    }
}