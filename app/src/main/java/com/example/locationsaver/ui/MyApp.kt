package com.example.locationsaver.ui

import android.app.Application
import com.example.locationsaver.di.module
import com.squareup.leakcanary.BuildConfig

import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            modules(listOf(module))
        }

    }
}