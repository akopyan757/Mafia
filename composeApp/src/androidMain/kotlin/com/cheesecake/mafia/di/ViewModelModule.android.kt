package com.cheesecake.mafia.di

import com.cheesecake.mafia.viewModel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

actual fun viewModelModule() = module {
    viewModel { MainViewModel(get(), null) }
}