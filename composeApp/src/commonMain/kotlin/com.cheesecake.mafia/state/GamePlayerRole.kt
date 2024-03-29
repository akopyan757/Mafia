package com.cheesecake.mafia.state

import androidx.compose.ui.graphics.Color
import com.cheesecake.mafia.common.Red
import com.cheesecake.mafia.common.RoleBlack
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.WhiteLight
import com.cheesecake.mafia.data.GamePlayerRole

fun GamePlayerRole.primaryColor(): Color {
    return when (this) {
        is GamePlayerRole.Red.Civilian -> Red.copy(alpha = 0.9f)
        is GamePlayerRole.Red -> Red
        is GamePlayerRole.Black -> RoleBlack
        is GamePlayerRole.White -> WhiteLight
        else -> White
    }
}

fun GamePlayerRole.secondaryColor(): Color {
    return when (this) {
        is GamePlayerRole.Red,
        is GamePlayerRole.Black -> White
        else -> Color.Black
    }
}

fun GamePlayerRole.priority(): Int {
    return when (this) {
        is GamePlayerRole.Red -> 2
        is GamePlayerRole.Black -> 1
        else -> 0
    }
}