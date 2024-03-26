package com.cheesecake.mafia

import android.app.Application
import com.cheesecake.mafia.di.networkModule
import com.cheesecake.mafia.di.repositoryModule
import com.cheesecake.mafia.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(networkModule(), repositoryModule(), viewModelModule())
        }
    }
}