package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.InteractiveScreenState
import com.cheesecake.mafia.repository.InteractiveGameRepository
import com.cheesecake.mafia.repository.ManageGameRepository
import com.cheesecake.mafia.repository.ReadGameRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val gameRepository: ReadGameRepository,
    private val manageGameRepository: ManageGameRepository,
    private val interactiveGameRepository: InteractiveGameRepository,
): ViewModel() {

    private val _gameItems = MutableStateFlow<List<GameData>>(emptyList())
    val gameItems: StateFlow<List<GameData>> = _gameItems

    init {
        interactiveGameRepository.saveState(InteractiveScreenState.Main)
    }

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