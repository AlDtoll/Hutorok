package com.example.hutorok

import android.app.Application
import org.koin.android.ext.android.startKoin

class App : Application() {

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))
        instance = this
    }
}