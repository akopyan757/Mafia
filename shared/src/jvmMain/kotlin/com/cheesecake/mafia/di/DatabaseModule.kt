package com.cheesecake.mafia.di

import com.cheesecake.mafia.database.DriverFactory
import com.cheesecake.mafia.database.IDriverFactory
import org.koin.dsl.module

actual fun databaseModule() = module {
    single<IDriverFactory> { DriverFactory() }
}