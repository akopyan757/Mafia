package com.cheesecake.mafia.di

import com.cheesecake.mafia.viewModel.LiveGameStandingViewModel
import com.cheesecake.mafia.viewModel.MainViewModel
import com.cheesecake.mafia.viewModel.NewGameStandingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

actual fun viewModelModule() = module {
    viewModel { MainViewModel() }
    viewModel { NewGameStandingViewModel(get()) }
    viewModel { LiveGameStandingViewModel(it.get()) }
}