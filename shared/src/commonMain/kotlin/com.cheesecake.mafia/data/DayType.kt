package com.cheesecake.mafia.data

import kotlinx.serialization.Serializable

@Serializable
enum class DayType(val order: Int) {
    Day(1),
    Night(0);
}

