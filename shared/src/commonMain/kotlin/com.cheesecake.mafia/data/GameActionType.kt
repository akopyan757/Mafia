package com.cheesecake.mafia.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface GameActionType {

    @Serializable
    data class Dead(val dayType: DayType): GameActionType {
        override fun dayType(): DayType = dayType
        override fun value(): String  = "Dead"
    }

    @Serializable
    enum class DayAction(
        private val value: String,
        private val iconRes: String
    ): GameActionType {
        Voted("Voted", "ic_like_button.xml"),
        Deleted("Deleted","ic_close.xml"),
        ThreeFouls("ThreeFouls", "ic_looks_3.xml");

        override fun iconRes(): String = iconRes
        override fun value(): String  = value
        override fun dayType(): DayType = DayType.Day
    }

    @Serializable
    enum class NightActon(val value: String, val role: GamePlayerRole): GameActionType {
        MafiaKilling("MafiaKilling", GamePlayerRole.Black.Mafia),
        DonChecking("DonChecking", GamePlayerRole.Black.Don),
        ManiacKilling("ManiacKilling", GamePlayerRole.White.Maniac),
        SheriffChecking("SheriffChecking", GamePlayerRole.Red.Sheriff),
        Doctor("Doctor", GamePlayerRole.Red.Doctor),
        ClientChoose("ClientChoose", GamePlayerRole.Red.Whore);

        override fun iconRes(): String = role.iconRes
        override fun value(): String  = value
        override fun dayType(): DayType = DayType.Night

        companion object {
            fun activeRoles(playerRoles: List<GamePlayerRole>): List<NightActon> {
                return entries.filter { action ->
                    playerRoles.contains(action.role) ||
                    (action == MafiaKilling && playerRoles.firstOrNull { it is GamePlayerRole.Black } != null)
                }
            }
        }
    }

    fun iconRes(): String = ""
    fun value(): String = ""
    fun dayType(): DayType
}