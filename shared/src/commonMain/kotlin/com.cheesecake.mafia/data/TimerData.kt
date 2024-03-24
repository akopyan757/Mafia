package com.cheesecake.mafia.data

import kotlinx.serialization.Serializable

@Serializable
data class TimerData(
    val value: Int,
    val total: Int,
    val active: Boolean,
)
