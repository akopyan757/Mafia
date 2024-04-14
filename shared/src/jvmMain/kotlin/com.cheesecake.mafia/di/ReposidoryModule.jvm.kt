package com.cheesecake.mafia.di

import com.cheesecake.mafia.repository.InteractiveGameRepository
import com.cheesecake.mafia.repository.InteractiveGameRepositoryImpl
import com.cheesecake.mafia.repository.LiveGameRepository
import com.cheesecake.mafia.repository.LiveGameRepositoryImpl
import com.cheesecake.mafia.repository.ManageGameRepository
import com.cheesecake.mafia.repository.ManageGameRepositoryImpl
import com.cheesecake.mafia.repository.PlayerRepository
import com.cheesecake.mafia.repository.PlayerRepositoryImpl
import com.cheesecake.mafia.repository.ReadGameRepository
import com.cheesecake.mafia.repository.ReadGameRepositoryImpl
import org.koin.dsl.module

actual fun repositoryModule() = module {
    single<PlayerRepository> { PlayerRepositoryImpl(get()) }
    single<ReadGameRepository> { ReadGameRepositoryImpl(get()) }
    single<ManageGameRepository> { ManageGameRepositoryImpl(get()) }
    single<InteractiveGameRepository> { InteractiveGameRepositoryImpl() }
    single<LiveGameRepository> { LiveGameRepositoryImpl(get()) }
}