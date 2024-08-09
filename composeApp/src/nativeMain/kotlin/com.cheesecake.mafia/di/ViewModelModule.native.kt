package com.cheesecake.mafia.di

import com.cheesecake.mafia.viewModel.MainViewModel
import org.koin.dsl.module

actual fun viewModelModule() = module {
    single { MainViewModel(get(), get(), null) }
}