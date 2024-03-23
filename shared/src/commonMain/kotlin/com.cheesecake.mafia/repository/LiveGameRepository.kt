package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.LiveGameData
import com.cheesecake.mafia.data.TimerData
import kotlinx.coroutines.flow.Flow

interface LiveGameRepository {
    fun listenLiveGame(): Flow<LiveGameData>
    fun saveLiveGame(data: LiveGameData)
    fun clearLiveState()
    fun updateTimer(data: TimerData)
    fun listenTimer(): Flow<TimerData>
}