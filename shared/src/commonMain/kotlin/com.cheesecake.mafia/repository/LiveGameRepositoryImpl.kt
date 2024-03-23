package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.LiveGameData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

internal class LiveGameRepositoryImpl: LiveGameRepository {

    private val _state = MutableStateFlow<LiveGameData?>(null)

    override fun saveLiveGame(data: LiveGameData) {
        _state.value = data
    }

    override fun listenLiveGame(): Flow<LiveGameData> {
        return _state.filterNotNull()
    }

    override fun clearLiveState() {
        _state.value = null
    }
}