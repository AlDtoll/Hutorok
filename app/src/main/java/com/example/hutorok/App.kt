package com.example.hutorok

import android.app.Application
import com.example.hutorok.network.ApiProvider
import org.koin.android.ext.android.startKoin

class App : Application() {

    companion object {
        lateinit var instance: App
        const val ADVENTURE_DEFAULT = "default"
        var CURRENT_ADVENTURE = ADVENTURE_DEFAULT
        var ALREADY_LOADED = false
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))
        ApiProvider.get()
        instance = this
    }
}