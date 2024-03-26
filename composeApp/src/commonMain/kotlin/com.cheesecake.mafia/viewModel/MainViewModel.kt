package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.InteractiveScreenState
import com.cheesecake.mafia.repository.InteractiveGameRepository
import com.cheesecake.mafia.repository.ReadGameRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val gameRepository: ReadGameRepository,
    interactiveGameRepository: InteractiveGameRepository?,
): ViewModel() {

    private val _gameItems = MutableStateFlow<List<GameData>>(emptyList())
    val gameItems: StateFlow<List<GameData>> = _gameItems
    val dates: StateFlow<List<String>> = _gameItems.map { games ->
        games.map { game -> game.date }.toSet().toList().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialValue = emptyList())

    init {
        interactiveGameRepository?.saveState(InteractiveScreenState.Main)
    }

    fun loadGames() {
        viewModelScope.launch {
            _gameItems.value = gameRepository.selectAll()
        }
    }
}