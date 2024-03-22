package com.cheesecake.mafia.di

import com.cheesecake.mafia.viewModel.FinishedGameViewModel
import com.cheesecake.mafia.viewModel.LiveGameViewModel
import com.cheesecake.mafia.viewModel.MainViewModel
import com.cheesecake.mafia.viewModel.NewGameViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

actual fun viewModelModule() = module {
    viewModel { MainViewModel(get()) }
    viewModel { NewGameViewModel(get()) }
    viewModel { LiveGameViewModel(it.get(), get()) }
    viewModel { FinishedGameViewModel(it.get(), get()) }
}