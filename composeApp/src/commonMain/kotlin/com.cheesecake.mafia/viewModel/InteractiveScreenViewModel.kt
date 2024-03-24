package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.data.TimerData
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

    val timer: StateFlow<TimerData> =
        interactiveGameRepository.listenTimer()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialValue = TimerData(0, 60, false))
}