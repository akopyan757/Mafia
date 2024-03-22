package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.common.ApiResult
import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.repository.GameRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FinishedGameViewModel(
    private val gameId: Long,
    private val gameRepository: GameRepository,
): ViewModel() {

    private val _gameDataResult = MutableStateFlow<ApiResult<GameData>>(ApiResult.Loading)
    val gameDataResult: StateFlow<ApiResult<GameData>> = _gameDataResult

    init {
        viewModelScope.launch {
            _gameDataResult.value = ApiResult.Loading
            val gameData = gameRepository.selectById(gameId)
            _gameDataResult.value = if (gameData != null) {
                ApiResult.Success(gameData)
            } else {
                ApiResult.Error(Throwable("Empty"))
            }
        }
    }
}