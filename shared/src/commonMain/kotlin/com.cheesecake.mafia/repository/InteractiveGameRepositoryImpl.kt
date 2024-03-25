package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.InteractiveScreenState
import com.cheesecake.mafia.data.SettingsData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

internal class InteractiveGameRepositoryImpl: InteractiveGameRepository {

    private val _state = MutableStateFlow<InteractiveScreenState>(InteractiveScreenState.None)
    private val _settings = MutableStateFlow(SettingsData())

    override fun saveState(data: InteractiveScreenState) {
        _state.value = data
    }

    override fun listenState(): Flow<InteractiveScreenState> {
        return _state.filterNotNull()
    }

    override fun clearState() {
        _state.value = InteractiveScreenState.None
    }

    override fun updateSettings(data: SettingsData) {
        _settings.value = data
    }

    override fun getSettings(): SettingsData {
        return _settings.value
    }

    override fun listenSettings() = _settings
}