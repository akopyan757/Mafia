package com.cheesecake.mafia.components.main

import com.cheesecake.mafia.data.LiveGameData

interface MainComponent {
    fun omStartNewGameClicked()
    fun onResumeGameClicked(liveGameData: LiveGameData)
    fun onGameClicked(gameId: Long)
}