package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.common.ApiResult
import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.InteractiveScreenState
import com.cheesecake.mafia.repository.InteractiveGameRepository
import com.cheesecake.mafia.repository.ReadGameRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FinishedGameViewModel(
    private val gameId: Long,
    private val gameRepository: ReadGameRepository,
    private val interactiveGameRepository: InteractiveGameRepository,
): ViewModel() {

    private val _gameDataResult = MutableStateFlow<ApiResult<GameData>>(ApiResult.Loading)
    val gameDataResult: StateFlow<ApiResult<GameData>> = _gameDataResult

    init {
        viewModelScope.launch {
            _gameDataResult.value = ApiResult.Loading
            val gameData = gameRepository.selectById(gameId)
            _gameDataResult.value = if (gameData != null) {
                interactiveGameRepository.saveState(InteractiveScreenState.FinishGame(gameData))
                ApiResult.Success(gameData)
            } else {
                interactiveGameRepository.saveState(InteractiveScreenState.FinishGame(null))
                ApiResult.Error(Throwable("Empty"))
            }
        }
    }
}