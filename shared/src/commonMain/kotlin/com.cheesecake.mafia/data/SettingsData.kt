package com.cheesecake.mafia.data

import kotlinx.serialization.Serializable

@Serializable
data class SettingsData(
    val showInteractive: Boolean = true,
    val timeValue: Int = 0,
    val timerTotal: Int = 60,
    val showTimer: Boolean = true,
    val showCandidates: Boolean = true,
)
