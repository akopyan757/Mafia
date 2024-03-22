package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.repository.GameRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val gameRepository: GameRepository
): ViewModel() {

    private val _gameItems = MutableStateFlow<List<GameData>>(emptyList())
    val gameItems: StateFlow<List<GameData>> = _gameItems

    fun deleteGame(gameId: Long) {
        viewModelScope.launch {
            gameRepository.deleteById(gameId)
            _gameItems.value = gameRepository.selectAll()
        }
    }

    fun loadGames() {
        viewModelScope.launch {
            _gameItems.value = gameRepository.selectAll()
        }
    }
}