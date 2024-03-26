package com.cheesecake.mafia.di

import com.cheesecake.mafia.repository.ReadGameRepository
import com.cheesecake.mafia.repository.ReadGameRepositoryImpl
import org.koin.dsl.module

actual fun repositoryModule() = module {
    single<ReadGameRepository> { ReadGameRepositoryImpl(get()) }
}