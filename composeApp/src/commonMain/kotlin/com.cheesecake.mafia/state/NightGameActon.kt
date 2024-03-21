package com.cheesecake.mafia.state

import kotlinx.serialization.Serializable

@Serializable
data class GameAction(
    val dayIndex: Int = 0,
    val actionType: GameActionType,
)

@Serializable
sealed interface GameActionType {

    @Serializable
    enum class NightActon(val role: GamePlayerRole): GameActionType {
        MafiaKilling(GamePlayerRole.Black.Mafia),
        DonChecking(GamePlayerRole.Black.Don),
        ManiacKilling(GamePlayerRole.White.Maniac),
        SheriffChecking(GamePlayerRole.Red.Sheriff),
        Doctor(GamePlayerRole.Red.Doctor),
        ClientChoose(GamePlayerRole.Red.Whore);

        override fun iconRes(): String = role.iconRes
        override fun dayType(): StageDayType = StageDayType.Night

        companion object {
            fun activeRoles(playerRoles: List<GamePlayerRole>): List<NightActon> {
                return entries.filter { action ->
                    playerRoles.contains(action.role) ||
                    (action == MafiaKilling && playerRoles.firstOrNull { it is GamePlayerRole.Black } != null)
                }
            }
        }
    }

    @Serializable
    enum class DayAction(private val iconRes: String): GameActionType {
        Voted("ic_like_button.xml"),
        Deleted("ic_close.xml"),
        ThreeFouls("ic_looks_3.xml");

        override fun iconRes(): String = iconRes
        override fun dayType(): StageDayType = StageDayType.Day
    }

    fun iconRes(): String = ""
    fun dayType(): StageDayType
}
