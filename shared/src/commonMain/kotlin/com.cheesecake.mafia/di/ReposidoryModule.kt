package com.cheesecake.mafia.di

import com.cheesecake.mafia.repository.LiveGameRepository
import com.cheesecake.mafia.repository.LiveGameRepositoryImpl
import com.cheesecake.mafia.repository.ManageGameRepository
import com.cheesecake.mafia.repository.ManageGameRepositoryImpl
import com.cheesecake.mafia.repository.ReadGameRepository
import com.cheesecake.mafia.repository.ReadGameRepositoryImpl
import com.cheesecake.mafia.repository.PlayerRepository
import com.cheesecake.mafia.repository.PlayerRepositoryImpl
import org.koin.dsl.module

fun repositoryModule() = module {
    single<PlayerRepository> { PlayerRepositoryImpl(get()) }
    single<ReadGameRepository> { ReadGameRepositoryImpl(get()) }
    single<ManageGameRepository> { ManageGameRepositoryImpl(get()) }
    single<LiveGameRepository> { LiveGameRepositoryImpl() }
}