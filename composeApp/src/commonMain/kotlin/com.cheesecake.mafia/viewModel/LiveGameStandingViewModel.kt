package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.state.GameAction
import com.cheesecake.mafia.state.GameActionType
import com.cheesecake.mafia.state.LivePlayerState
import com.cheesecake.mafia.state.GamePlayerRole
import com.cheesecake.mafia.state.NewGamePlayerItem
import com.cheesecake.mafia.state.LiveStage
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class LiveGameState(
    val gameActive: Boolean = false,
    val showRoles: Boolean = true,
    val players: List<LivePlayerState> = emptyList(),
    val round: Int = 0,
    val stage: LiveStage = LiveStage.Start,
    val queueStage: ArrayDeque<LiveStage> = ArrayDeque(40),
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

class LiveGameStandingViewModel(
    players: List<NewGamePlayerItem>,
): ViewModel() {

    private val _state = MutableStateFlow(LiveGameState())

    val state: StateFlow<LiveGameState> get() = _state

    init {
        val startPlayers = players.map { item ->
            LivePlayerState(item.player.id, item.number, item.player.name, item.role)
        }
        val alivePlayers = startPlayers.filter { it.isAlive }
        val startQueue = _state.value.queueStage.let { queue ->
            queue.addAll(alivePlayers.map { LiveStage.Day.Speech(it.number) })
            queue.add(LiveStage.Day.Vote())
            queue
        }
        changeState { state ->
            state.copy(
                players = startPlayers, queueStage = startQueue,
            )
        }

        nextStage()
    }

    fun startOrResumeGame() {
        changeState { state -> state.copy(gameActive = true) }
    }

    fun pauseGame() {
        changeState { state -> state.copy(gameActive = false) }
    }

    fun stopGame(time: Int) {
        changeState { state -> state.copy(gameActive = false) }
    }

    fun changeShowRolesState(showRoles: Boolean) {
        changeState { state -> state.copy(showRoles = showRoles) }
    }

    fun nextStage() {
        changeState { state ->
            val queue = state.queueStage
            val currentStage = queue.removeFirstOrNull() ?: return@changeState state
            if (currentStage is LiveStage.Day.Vote) {
                if (state.voteCandidates.isEmpty()) {
                    state.copy(queueStage = queue, stage = LiveStage.Night())
                } else {
                    state.copy(queueStage = queue, stage = currentStage)
                }
            } else if (currentStage is LiveStage.Night) {
                state.copy(
                    round = state.round + 1,
                    stage = currentStage,
                    players = state.players.map { player -> player.copy(isClient = false) },
                    voteCandidates = emptyList(),
                )
            } else {
                state.copy(queueStage = queue, stage = currentStage)
            }
        }
    }

    fun addVotedCandidate(playerNumber: Int) {
        changeState { state ->
            state.copy(voteCandidates = state.voteCandidates + listOf(playerNumber))
        }
    }

    fun reVotePlayers(votedPlayers: List<Int>) {
        changeState { state ->
            val queue = state.queueStage
            queue.addAll(votedPlayers.map { LiveStage.Day.Speech(it, candidateForElimination = true) })
            queue.add(LiveStage.Day.Vote(reVote = true))
            state.copy(
                voteCandidates = votedPlayers,
                queueStage = queue,
            )
        }
        nextStage()
    }

    fun votePlayers(votedPlayers: List<Int>) {
        changeState { state ->
            val queue = state.queueStage
            queue.addAll(votedPlayers.map { LiveStage.Day.LastVotedSpeech(it) })
            queue.add(LiveStage.Night())
            state.copy(
                queueStage = queue,
                players = state.players.changeItems(votedPlayers) { player ->
                    player.copy(
                        isAlive = false,
                        actions = player.actions + listOf(
                            GameAction(state.round, GameActionType.DayAction.Voted)
                        )
                    )
                }
            )
        }
        nextStage()
    }

    fun getNightGameActions(onlyActive: Boolean = false): List<GameActionType.NightActon> {
        val roles = _state.value.players.filter { it.isAlive || !onlyActive }.map { it.role }
        return GameActionType.NightActon.activeRoles(roles)
    }

    fun changeNightAction(actions: Map<GameActionType.NightActon, Int>) {
        changeState { state -> state.copy(nightActions = actions) }
    }

    fun acceptNightActions() {
        changeState { state ->
            val speechPlayers = state.players.filter { it.isAlive && !it.isClient }
            val queue = state.queueStage
            queue.addAll(state.lastKilledPlayers.map { LiveStage.Day.LastDeathSpeech(it) })
            queue.addAll(speechPlayers.map { LiveStage.Day.Speech(it.number) })
            queue.add(LiveStage.Day.Vote())
            state.copy(queueStage = queue).copyWithAcceptanceNightActions()
        }
        nextStage()
    }

    fun changeFoulsCount(playerNumber: Int, fouls: Int) {
        changeState { state ->
            state.copy(players = state.players.changeItem(playerNumber) { it.copy(fouls = fouls) })
        }
    }

    fun acceptDeletePlayers(skipToNight: Boolean) {
        val numbers = state.value.deleteCandidates
        if (numbers.isEmpty()) return
        val queue = state.value.queueStage
        if (skipToNight) {
            queue.clear()
            queue.add(LiveStage.Night())
        }
        changeState { state ->
            state.copy(
                queueStage = queue,
                players = state.players.changeItems(numbers) { player ->
                    player.copy(
                        isAlive = false,
                        isDeleted = true,
                        isClient = false,
                        fouls = 4,
                        actions = player.actions + listOf(
                            GameAction(state.round, GameActionType.DayAction.Deleted)
                        ),
                    )
                }
            )
        }
        if (skipToNight) {
            nextStage()
        }
    }

    private fun LiveGameState.copyWithAcceptanceNightActions(): LiveGameState {
        val players = players.toMutableList()
        nightActions.forEach { (nightAction, number) ->
            val index = players.indexOfFirst { it.number == number }
            if (index != -1) {
                val player = players[index]
                players[index] = player.copy(
                    actions = player.actions + listOf(GameAction(round, nightAction)),
                    isClient = player.number == lastClientPlayer,
                    isAlive = player.isAlive && player.number !in lastKilledPlayers,
                )
            }
        }
        return copy(
            players = players.toList(),
            nightActions = emptyMap(),
        )
    }

    private fun changeState(transform: (LiveGameState) -> LiveGameState) {
        _state.value = transform(_state.value)
    }
}