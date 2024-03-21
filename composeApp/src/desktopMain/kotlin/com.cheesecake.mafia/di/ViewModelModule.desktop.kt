package com.cheesecake.mafia.di

import com.cheesecake.mafia.viewModel.LiveGameStandingViewModel
import com.cheesecake.mafia.viewModel.MainViewModel
import com.cheesecake.mafia.viewModel.NewGameStandingViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun viewModelModule(): Module = module {
    single { MainViewModel() }
    single { NewGameStandingViewModel(get()) }
    single { params -> LiveGameStandingViewModel(params[0]) }
}