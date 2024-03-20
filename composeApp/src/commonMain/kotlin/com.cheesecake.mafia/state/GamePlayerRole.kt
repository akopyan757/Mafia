package com.cheesecake.mafia.state

import androidx.compose.ui.graphics.Color
import com.cheesecake.mafia.common.Red
import com.cheesecake.mafia.common.RoleBlack
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.WhiteLight

@kotlinx.serialization.Serializable
sealed class GamePlayerRole(val name: String, val iconRes: String) {
    data object None: GamePlayerRole("", "")
    sealed class Red(name: String, emoji: String) : GamePlayerRole(name, emoji) {
        data object Сivilian : Red("Мирный", "ic_user.xml")
        data object Sheriff : Red("Шериф", "ic_role_sheriff.xml")
        data object Doctor : Red("Доктор", "ic_role_doctor.xml")
        data object Whore : Red("Путана", "ic_role_whore.xml")
    }
    sealed class Black(name: String, emoji: String) : GamePlayerRole(name, emoji) {
        data object Mafia: Black("Мафия", "ic_role_mafia.xml")
        data object Don : Black("Дон", "ic_role_don.xml")
    }
    sealed class White(name: String, emoji: String) : GamePlayerRole(name, emoji) {
        data object Maniac : White("Маньяк","ic_role_maniac.xml")
    }

    companion object {
        fun values() = listOf(
            GamePlayerRole.Red.Сivilian,
            GamePlayerRole.Red.Sheriff,
            GamePlayerRole.Red.Doctor,
            GamePlayerRole.Red.Whore,
            GamePlayerRole.Black.Mafia,
            GamePlayerRole.Black.Don,
            GamePlayerRole.White.Maniac,
        )
    }
}

fun GamePlayerRole.primaryColor(): Color {
    return when (this) {
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