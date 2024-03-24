package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.InteractiveScreenState
import com.cheesecake.mafia.data.TimerData
import kotlinx.coroutines.flow.Flow

interface InteractiveGameRepository {
    fun listenState(): Flow<InteractiveScreenState>
    fun saveState(data: InteractiveScreenState)
    fun clearState()
    fun updateTimer(data: TimerData)
    fun listenTimer(): Flow<TimerData>
}