package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.data.LiveGameData
import com.cheesecake.mafia.repository.LiveGameRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SecondScreenViewModel(
    liveGameRepository: LiveGameRepository,
): ViewModel() {

    val state: StateFlow<LiveGameData> =
        liveGameRepository
            .listenLiveGame()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialValue = LiveGameData())
}