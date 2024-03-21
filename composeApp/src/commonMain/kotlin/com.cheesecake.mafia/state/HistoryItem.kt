package com.cheesecake.mafia.state

import kotlinx.serialization.Serializable

@Serializable
sealed class HistoryItem(private val _id: Int, val type: StageDayType) {
    @Serializable
    data class Start(val id: Int): HistoryItem(id, StageDayType.Day) {
        override val text: String
            get() = "Старт игры"
    }

    @Serializable
    data class ReVote(val candidates: List<Int>, val id: Int): HistoryItem(id, StageDayType.Day) {
        override val text: String
            get() = "Переголосование между игроками ${candidates.joinToString(separator = ", ")}"
    }
    @Serializable
    data class Elimination(val candidates: List<Int>, val id: Int): HistoryItem(id,
        StageDayType.Day
    ) {
        override val text: String
            get() = if (candidates.size < 2)
                "Голосованием покидает игрок ${candidates.joinToString(", ")}"
            else
                "Голосованием покидают игроки  ${candidates.joinToString(", ")}"
    }
    @Serializable
    data class NightAction(val nightAction: GameActionType.NightActon, val player: Int, val id: Int): HistoryItem(id,
        StageDayType.Night
    ) {
        override val text: String
            get() = when (nightAction) {
                GameActionType.NightActon.MafiaKilling -> "Мафия стреляет в игрока $player"
                GameActionType.NightActon.ManiacKilling -> "Маньяк стреляет в игрока $player"
                GameActionType.NightActon.Doctor -> "Доктор пытается спасти игрока $player"
                GameActionType.NightActon.DonChecking -> "Дон проверяет игрока $player"
                GameActionType.NightActon.SheriffChecking -> "Шериф проверяет игрока $player"
                GameActionType.NightActon.ClientChoose -> "Путана выбрала клиентом игрока $player"
            }
    }

    @Serializable
    data class Nomination(val playerFrom: Int, val playerTo: Int, val id: Int): HistoryItem(id,
        StageDayType.Day
    ) {
        override val text: String get() = "Игрок $playerFrom выставил игрока $playerTo"
    }
    @Serializable
    data class Fouls(val player: Int, val fouls: Int, val id: Int): HistoryItem(id,
        StageDayType.Day
    ) {
        override val text: String get() = "Игрок $player получил $fouls фол"
    }

    @Serializable
    data class DeletePlayer(val player: Int, val dayType: StageDayType, val id: Int): HistoryItem(id, dayType) {
        override val text: String get() = "Игрок $player удален"
    }

    open val text: String = ""

    fun id(): Int = _id
}