package com.cheesecake.mafia.di

import com.cheesecake.mafia.viewModel.LiveGameStandingViewModel
import com.cheesecake.mafia.viewModel.MainViewModel
import com.cheesecake.mafia.viewModel.NewGameStandingViewModel
import org.koin.dsl.module

actual fun viewModelModule() = module {
    single { MainViewModel() }
    single { NewGameStandingViewModel(get()) }
    single { LiveGameStandingViewModel(it.get()) }
}