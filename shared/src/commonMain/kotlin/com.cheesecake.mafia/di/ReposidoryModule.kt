package com.cheesecake.mafia.di

import com.cheesecake.mafia.repository.PlayerRepository
import com.cheesecake.mafia.repository.PlayerRepositoryImpl
import org.koin.dsl.module

fun repositoryModule() = module {
    single<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }
}