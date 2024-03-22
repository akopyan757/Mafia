package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.repository.ManageGameRepository
import com.cheesecake.mafia.repository.ReadGameRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val gameRepository: ReadGameRepository,
    private val manageGameRepository: ManageGameRepository,
): ViewModel() {

    private val _gameItems = MutableStateFlow<List<GameData>>(emptyList())
    val gameItems: StateFlow<List<GameData>> = _gameItems

    fun deleteGame(gameId: Long) {
        viewModelScope.launch {
            manageGameRepository.deleteById(gameId)
            _gameItems.value = gameRepository.selectAll()
        }
    }

    fun loadGames() {
        viewModelScope.launch {
            _gameItems.value = gameRepository.selectAll()
        }
    }
}