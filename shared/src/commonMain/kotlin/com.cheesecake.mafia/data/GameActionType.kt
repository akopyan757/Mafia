package com.cheesecake.mafia.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface GameActionType {

    @Serializable
    data class Dead(val dayType: DayType): GameActionType {
        override fun dayType(): DayType = dayType
    }

    @Serializable
    enum class DayAction(val iconRes: String): GameActionType {
        @SerialName("Voted") Voted("ic_like_button.xml"),
        @SerialName("Deleted") Deleted("ic_close.xml"),
        @SerialName("ThreeFouls") ThreeFouls("ic_looks_3.xml");

        override fun iconRes(): String = iconRes
        override fun dayType(): DayType = DayType.Day
    }

    @Serializable
    enum class NightActon(val role: GamePlayerRole): GameActionType {
        @SerialName("MafiaKilling") MafiaKilling(GamePlayerRole.Black.Mafia),
        @SerialName("DonChecking") DonChecking(GamePlayerRole.Black.Don),
        @SerialName("ManiacKilling") ManiacKilling(GamePlayerRole.White.Maniac),
        @SerialName("SheriffChecking") SheriffChecking(GamePlayerRole.Red.Sheriff),
        @SerialName("Doctor") Doctor(GamePlayerRole.Red.Doctor),
        @SerialName("ClientChoose") ClientChoose(GamePlayerRole.Red.Whore);

        override fun iconRes(): String = role.iconRes
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
    fun dayType(): DayType
}