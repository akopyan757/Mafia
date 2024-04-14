package com.cheesecake.mafia.state

import com.cheesecake.mafia.data.LiveGameData
import kotlinx.serialization.Serializable

@Serializable
sealed class StartData {
    data class NewGame(
        val items: List<NewGamePlayerItem> = emptyList(),
        val date: String = "",
        val title: String = "",
    ): StartData()

    data class ResumeExistGame(
        val data: LiveGameData
    ): StartData()
}
