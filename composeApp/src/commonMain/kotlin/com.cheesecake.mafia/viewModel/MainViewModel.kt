package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.InteractiveScreenState
import com.cheesecake.mafia.data.LiveGameData
import com.cheesecake.mafia.data.onSuccess
import com.cheesecake.mafia.repository.InteractiveGameRepository
import com.cheesecake.mafia.repository.LiveGameRepository
import com.cheesecake.mafia.repository.ReadGameRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val readGameRepository: ReadGameRepository,
    private val liveGameRepository: LiveGameRepository,
    interactiveGameRepository: InteractiveGameRepository?,
): ViewModel() {

    private val _liveGames = MutableStateFlow<List<LiveGameData>>(emptyList())
    private val _finishedGames = MutableStateFlow<List<GameData>>(emptyList())

    val liveGames = _liveGames.asStateFlow()
    val finishedGames: StateFlow<List<GameData>> = _finishedGames

    val finishedGameDates: StateFlow<List<String>> = _finishedGames.map { games ->
        games.map { game -> game.date }.toSet().toList().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialValue = emptyList())

    init {
        interactiveGameRepository?.saveState(InteractiveScreenState.Main)
    }

    fun loadGames() {
        viewModelScope.launch {
            _finishedGames.value = readGameRepository.selectAll()
            liveGameRepository.selectAll().collect { result ->
                result.onSuccess { _liveGames.value = it }
            }
        }
    }
}