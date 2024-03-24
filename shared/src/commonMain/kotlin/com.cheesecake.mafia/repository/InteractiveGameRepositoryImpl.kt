package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.InteractiveScreenState
import com.cheesecake.mafia.data.TimerData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

internal class InteractiveGameRepositoryImpl: InteractiveGameRepository {

    private val _state = MutableStateFlow<InteractiveScreenState>(InteractiveScreenState.None)
    private val _timer = MutableStateFlow(TimerData(0, 60, false))

    override fun saveState(data: InteractiveScreenState) {
        _state.value = data
    }

    override fun listenState(): Flow<InteractiveScreenState> {
        return _state.filterNotNull()
    }

    override fun clearState() {
        _state.value = InteractiveScreenState.None
    }

    override fun updateTimer(data: TimerData) {
        _timer.value = data
    }

    override fun listenTimer() = _timer


}