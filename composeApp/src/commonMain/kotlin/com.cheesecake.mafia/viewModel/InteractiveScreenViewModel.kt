package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.data.SettingsData
import com.cheesecake.mafia.repository.InteractiveGameRepository
import com.cheesecake.mafia.data.InteractiveScreenState
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class InteractiveScreenViewModel(
    interactiveGameRepository: InteractiveGameRepository,
): ViewModel() {

    val state: StateFlow<InteractiveScreenState> =
        interactiveGameRepository.listenState()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialValue = InteractiveScreenState.Main)

    val settings: StateFlow<SettingsData> =
        interactiveGameRepository.listenSettings()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialValue = SettingsData())
}