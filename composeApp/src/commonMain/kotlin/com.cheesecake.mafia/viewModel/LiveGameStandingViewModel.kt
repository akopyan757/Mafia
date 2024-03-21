package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.state.FinishedGameProtocolState
import com.cheesecake.mafia.state.GameAction
import com.cheesecake.mafia.state.GameActionType
import com.cheesecake.mafia.state.GameFinishResult
import com.cheesecake.mafia.state.HistoryItem
import com.cheesecake.mafia.state.LiveGameState
import com.cheesecake.mafia.state.LivePlayerState
import com.cheesecake.mafia.state.LiveStage
import com.cheesecake.mafia.state.NewGamePlayerItem
import com.cheesecake.mafia.state.StageDayType
import com.cheesecake.mafia.state.buildFinishedProtocol
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.round

class LiveGameStandingViewModel(
    players: List<NewGamePlayerItem>,
): ViewModel() {

    companion object {
        const val HISTORY_SIZE = 5
    }

    private val _state = MutableStateFlow(LiveGameState())
    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    private val _undoStack = MutableStateFlow(ArrayDeque<LiveGameState>(HISTORY_SIZE))
    private val _redoStack = MutableStateFlow(ArrayDeque<LiveGameState>(HISTORY_SIZE))
    private val _gameActive = MutableStateFlow(false)

    val state: StateFlow<LiveGameState> get() = _state
    val history: StateFlow<List<HistoryItem>> get() = _history
    val undoStack: StateFlow<ArrayDeque<LiveGameState>> get() = _undoStack
    val redoStack: StateFlow<ArrayDeque<LiveGameState>> get() = _redoStack
    val gameActive: StateFlow<Boolean> get() = _gameActive

    init {
        val startPlayers = players.map { item ->
            LivePlayerState(item.player.id, item.number, item.player.name, item.role)
        }
        val alivePlayers = startPlayers.filter { it.isAlive }
        val startQueue = _state.value.queueStage
            .push(alivePlayers.map { LiveStage.Day.Speech(it.number) })
            .push(LiveStage.Day.Vote())
        addHistoryItem(HistoryItem.Start(getStateId()))
        changeStateAndNext(historyCached = false) { state ->
            state.copy(
                players = startPlayers, queueStage = startQueue,
            )
        }
    }

    fun undoState() {
        val undoHistory = _undoStack.value
        val state = undoHistory.removeLastOrNull() ?: return
        val redoHistory = _redoStack.value
        _history.value = _history.value.filterNot { it.id() == getStateId() }
        _state.value = state
        redoHistory.addLast(state)
        _redoStack.value = redoHistory
    }

    fun redoState() {
        val redoHistory = _redoStack.value
        val state = redoHistory.removeLastOrNull() ?: return
        val undoHistory = _undoStack.value
        _state.value = state.copy(id = state.id + 1)
        undoHistory.addLast(state)
        _undoStack.value = undoHistory
    }

    fun startOrResumeGame() {
        _gameActive.value = true
    }

    fun pauseGame() {
        _gameActive.value = false
    }

    fun buildFinishedProtocol(time: Int, winner: GameFinishResult): FinishedGameProtocolState? {
        return buildFinishedProtocol(
            liveGameState = _state.value,
            history = _history.value,
            finishResult = winner,
            totalTime = time,
        )
    }

    fun stopGame() {
        _gameActive.value = false
    }

    fun changeStateAndNext(
        historyCached: Boolean = false,
        transform: ((LiveGameState) -> LiveGameState)? = null
    ) {
        changeState(cached = historyCached) { oldState ->
            val state = transform?.let { it(oldState) } ?: oldState
            val (queue, currentStage) = state.queueStage.popFirst()
            currentStage?.let {
                return@let if (currentStage is LiveStage.Day.Vote) {
                    if (state.voteCandidates.isEmpty()) {
                        state.copy(queueStage = queue, stage = LiveStage.Night())
                    } else {
                        state.copy(queueStage = queue, stage = currentStage.copy(candidates = state.voteCandidates))
                    }
                } else if (currentStage is LiveStage.Night) {
                    state.copy(
                        round = state.round + 1,
                        stage = currentStage,
                        queueStage = queue,
                        players = state.players.map { player -> player.copy(isClient = false) },
                        voteCandidates = emptyList(),
                    )
                } else {
                    state.copy(queueStage = queue, stage = currentStage)
                }
            } ?: run {
                state.copy(queueStage = queue)
            }
        }
    }

    fun addVotedCandidate(playerNumber: Int) {
        changeState(cached = true) { state ->
            state.copy(voteCandidates = state.voteCandidates + listOf(playerNumber))
        }
        val stage = _state.value.stage
        if (stage is LiveStage.Day.Speech) {
            addHistoryItem(HistoryItem.Nomination(stage.playerNumber, playerNumber, getStateId()))
        }
    }

    fun reVotePlayers(votedPlayers: List<Int>) {
        changeStateAndNext(historyCached = true) { state ->
            val queue = state.queueStage
                .push(votedPlayers.map { LiveStage.Day.Speech(it, candidateForElimination = true) })
                .push(LiveStage.Day.Vote(reVote = true))
            state.copy(
                voteCandidates = votedPlayers,
                queueStage = queue,
            )
        }
        addHistoryItem(HistoryItem.ReVote(votedPlayers, getStateId()))
    }

    fun votePlayers(votedPlayers: List<Int>) {
        changeStateAndNext(historyCached = true)  { state ->
            val queue = state.queueStage
                .push(votedPlayers.map { LiveStage.Day.LastVotedSpeech(it) })
                .push(LiveStage.Night())
            state.copy(
                queueStage = queue,
                players = state.players.changeItems(votedPlayers) { player ->
                    player.copy(
                        isAlive = false,
                        actions = player.actions + listOf(
                            GameAction(state.round, GameActionType.DayAction.Voted),
                            GameAction(state.round + 1, GameActionType.Dead(StageDayType.Day))
                        )
                    )
                }
            )
        }
        addHistoryItem(HistoryItem.Elimination(votedPlayers, getStateId()))
    }

    fun getNightGameActions(onlyActive: Boolean = false): List<GameActionType.NightActon> {
        val roles = _state.value.players.filter { it.isAlive || !onlyActive }.map { it.role }
        return GameActionType.NightActon.activeRoles(roles)
    }

    fun changeNightAction(actions: Map<GameActionType.NightActon, Int>) {
        changeState { state -> state.copy(nightActions = actions) }
    }

    fun acceptNightActions() {
        val nightActions = _state.value.nightActions
        changeStateAndNext(historyCached = true) { state ->
            val speechPlayers = state.players.filter { it.isAlive && !it.isClient }
            val queue = state.queueStage
                .push(state.lastKilledPlayers.map { LiveStage.Day.LastDeathSpeech(it) })
                .push(speechPlayers.map { LiveStage.Day.Speech(it.number) })
                .push(LiveStage.Day.Vote())
            state.copy(queueStage = queue).copyWithAcceptanceNightActions()
        }
        addHistoryItems(
            nightActions.map { (action, playerNumber) ->
                HistoryItem.NightAction(action, playerNumber, getStateId())
            }
        )
    }

    fun changeFoulsCount(playerNumber: Int, fouls: Int) {
        changeState(cached = true) { state ->
            state.copy(players = state.players.changeItem(playerNumber) { it.copy(fouls = fouls) })
        }
        if (fouls < 4) {
            addHistoryItem(HistoryItem.Fouls(playerNumber, fouls, getStateId()))
        }
    }

    fun acceptDeletePlayers(skipToNight: Boolean) {
        val numbers = state.value.deleteCandidates
        if (numbers.isEmpty()) return
        val transform: (LiveGameState) -> LiveGameState = { state ->
            state.copy(
                queueStage = if (skipToNight) {
                    listOf(LiveStage.Night())
                } else {
                    state.queueStage
                },
                players = state.players.changeItems(numbers) { player ->
                    player.copy(
                        isAlive = false,
                        isDeleted = true,
                        isClient = false,
                        fouls = 4,
                        actions = player.actions +
                            listOf(GameAction(state.round, GameActionType.DayAction.Deleted)) +
                            listOf(GameAction(state.round, GameActionType.Dead(state.stage.type))),
                    )
                }
            )
        }
        if (skipToNight) {
            changeStateAndNext(historyCached = true, transform = transform)
        } else {
            changeState(cached = true, transform = transform)
        }
        addHistoryItems(
            numbers.map { playerNumber ->
                HistoryItem.DeletePlayer(playerNumber, _state.value.stage.type, getStateId())
            }
        )
    }

    private fun List<LiveStage>.push(items: List<LiveStage>): List<LiveStage> {
        return this + items
    }

    private fun List<LiveStage>.push(item: LiveStage): List<LiveStage> {
        return this + listOf(item)
    }

    private fun List<LiveStage>.popFirst(): Pair<List<LiveStage>, LiveStage?> {
        val items = this.toMutableList()
        val last = items.removeFirstOrNull() ?: return emptyList<LiveStage>() to null
        return items.toList() to last
    }

    private fun LiveGameState.copyWithAcceptanceNightActions(): LiveGameState {
        val players = players.toMutableList()
        nightActions.forEach { (nightAction, number) ->
            val index = players.indexOfFirst { it.number == number }
            val isKilled = number in lastKilledPlayers
            if (index != -1) {
                val player = players[index]
                players[index] = player.copy(
                    actions = player.actions
                        + listOf(GameAction(round, nightAction)) +
                        if (isKilled) listOf(GameAction(round, GameActionType.Dead(StageDayType.Night))) else emptyList(),
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

    private fun addHistoryItem(historyItem: HistoryItem) {
        _history.value += historyItem
    }

    private fun addHistoryItems(historyItems: List<HistoryItem>) {
        _history.value += historyItems
    }

    private fun changeState(
        cached: Boolean = false,
        transform: (LiveGameState) -> LiveGameState
    ) {
        val oldState = _state.value
        _state.value = transform(oldState).incStateId()
        if (cached) {
            val undoHistory = _undoStack.value
            if (undoHistory.size >= HISTORY_SIZE) {
                undoHistory.removeFirstOrNull()
            }
            undoHistory.addLast(oldState)
            _undoStack.value = undoHistory
            val redoHistory = _redoStack.value
            redoHistory.clear()
            _redoStack.value = redoHistory
        }
    }

    private fun LiveGameState.incStateId(): LiveGameState {
        return copy(id = id + 1)
    }

    private fun getStateId(): Int {
        return _state.value.id
    }
}