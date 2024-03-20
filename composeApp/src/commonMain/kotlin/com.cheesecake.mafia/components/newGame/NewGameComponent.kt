package com.cheesecake.mafia.components.newGame

import com.cheesecake.mafia.state.NewGamePlayerItem

interface NewGameComponent {
    fun onBackClicked()
    fun onStartGameClicked(items: List<NewGamePlayerItem>) {}
}