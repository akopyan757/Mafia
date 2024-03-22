package com.cheesecake.mafia.di

import com.cheesecake.mafia.viewModel.FinishedGameViewModel
import com.cheesecake.mafia.viewModel.LiveGameViewModel
import com.cheesecake.mafia.viewModel.MainViewModel
import com.cheesecake.mafia.viewModel.NewGameViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun viewModelModule(): Module = module {
    single { MainViewModel(get(), get()) }
    factory { NewGameViewModel(get()) }
    factory { params -> LiveGameViewModel(params[0], get()) }
    factory { params -> FinishedGameViewModel(params[0], get()) }
}