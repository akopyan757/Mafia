package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.InteractiveScreenState
import com.cheesecake.mafia.data.SettingsData
import kotlinx.coroutines.flow.Flow

interface InteractiveGameRepository {
    fun listenState(): Flow<InteractiveScreenState>
    fun saveState(data: InteractiveScreenState)
    fun clearState()
    fun updateSettings(data: SettingsData)
    fun getSettings(): SettingsData
    fun listenSettings(): Flow<SettingsData>
}