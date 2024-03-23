package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.LiveGameData
import kotlinx.coroutines.flow.Flow

interface LiveGameRepository {
    fun saveLiveGame(data: LiveGameData)

    fun listenLiveGame(): Flow<LiveGameData>

    fun clearLiveState()
}