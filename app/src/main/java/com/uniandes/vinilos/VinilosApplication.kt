package com.uniandes.vinilos

import android.app.Application
import com.uniandes.vinilos.di.networkModule
import com.uniandes.vinilos.di.repositoryModule
import com.uniandes.vinilos.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class VinilosApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@VinilosApplication)
            modules(listOf(networkModule, repositoryModule, viewModelModule))
        }
    }
} 