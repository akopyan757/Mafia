package com.cheesecake.mafia.di

import com.cheesecake.mafia.viewModel.FinishedGameViewModel
import com.cheesecake.mafia.viewModel.LiveGameViewModel
import com.cheesecake.mafia.viewModel.MainViewModel
import com.cheesecake.mafia.viewModel.NewGameViewModel
import org.koin.dsl.module

actual fun viewModelModule() = module {
    single { MainViewModel(get(), get()) }
    factory { NewGameViewModel(get()) }
    factory { LiveGameViewModel(it.get(), get(), get()) }
    factory { FinishedGameViewModel(it.get(), get()) }
}