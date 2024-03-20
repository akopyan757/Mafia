package com.cheesecake.mafia.state

data class LiveGameState(
    val id: Int = 1,
    val players: List<LivePlayerState> = emptyList(),
    val round: Int = 0,
    val stage: LiveStage = LiveStage.Start,
    val queueStage: List<LiveStage> = emptyList(),
    val voteCandidates: List<Int> = emptyList(),
    val nightActions: Map<GameActionType.NightActon, Int> = emptyMap(),
) {

    val totalVotes: Int
        get() = players.map { it.isAlive && !it.isClient }.size

    val deleteCandidates: List<Int>
        get() = players.filter { it.isAlive && it.fouls == 4 }.map { it.number }

    val lastClientPlayer: Int?
        get() {
            val clientChosen = nightActions[GameActionType.NightActon.ClientChoose]
            return clientChosen?.takeIf { clientChosen !in lastKilledPlayers }
        }

    val lastKilledPlayers: List<Int>
        get() {
            val roles = players.associateBy(keySelector = { it.role }) { it.number }
            val mafiaKilling = nightActions[GameActionType.NightActon.MafiaKilling]
            val maniacKilling = nightActions[GameActionType.NightActon.ManiacKilling]
            val clientChosen = nightActions[GameActionType.NightActon.ClientChoose]
            val doctorSaving = nightActions[GameActionType.NightActon.Doctor]
            return if (
                roles[GamePlayerRole.Red.Whore] == mafiaKilling ||
                roles[GamePlayerRole.Red.Whore] == maniacKilling
            ) {
                listOfNotNull(mafiaKilling, maniacKilling, clientChosen)
                    .filterNot { number -> doctorSaving == number }
            } else {
                listOfNotNull(mafiaKilling, maniacKilling)
                    .filterNot { number -> doctorSaving == number || clientChosen == number }
            }
        }
}