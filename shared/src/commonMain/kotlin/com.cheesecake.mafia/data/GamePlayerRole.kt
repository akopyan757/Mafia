package com.cheesecake.mafia.data

import kotlinx.serialization.Serializable

@Serializable
sealed class GamePlayerRole {
    abstract val iconRes: String
    abstract val name: String

    @Serializable
    data object None: GamePlayerRole() {
        override val iconRes: String = ""
        override val name: String = ""
    }

    @Serializable
    sealed class Red(override val name: String, override val iconRes: String): GamePlayerRole() {

        @Serializable
        data object Сivilian : Red("Мирный", "ic_user.xml")

        @Serializable
        data object Sheriff : Red("Шериф", "ic_role_sheriff.xml")

        @Serializable
        data object Doctor : Red("Доктор", "ic_role_doctor.xml")

        @Serializable
        data object Whore : Red("Путана", "ic_role_whore.xml")
    }

    @Serializable
    sealed class Black(override val name: String, override val iconRes: String) : GamePlayerRole() {

        @Serializable
        data object Mafia: Black("Мафия", "ic_role_mafia.xml")

        @Serializable
        data object Don : Black("Дон", "ic_role_don.xml")
    }

    @Serializable
    sealed class White(override val name: String, override val iconRes: String) : GamePlayerRole() {

        @Serializable
        data object Maniac : White("Маньяк","ic_role_maniac.xml")
    }
}

fun roleValues() = listOf(
    GamePlayerRole.Red.Сivilian,
    GamePlayerRole.Red.Sheriff,
    GamePlayerRole.Red.Doctor,
    GamePlayerRole.Red.Whore,
    GamePlayerRole.Black.Mafia,
    GamePlayerRole.Black.Don,
    GamePlayerRole.White.Maniac,
)