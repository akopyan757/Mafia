package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.common.changeItem
import com.cheesecake.mafia.common.changeItems
import com.cheesecake.mafia.data.DayType
import com.cheesecake.mafia.data.GameAction
import com.cheesecake.mafia.data.GameActionType
import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.GameFinishResult
import com.cheesecake.mafia.repository.ManageGameRepository
import com.cheesecake.mafia.state.HistoryItem
import com.cheesecake.mafia.data.LiveGameData
import com.cheesecake.mafia.data.LivePlayerData
import com.cheesecake.mafia.data.LiveStage
import com.cheesecake.mafia.data.InteractiveScreenState
import com.cheesecake.mafia.data.TimerData
import com.cheesecake.mafia.repository.InteractiveGameRepository
import com.cheesecake.mafia.state.StartGameData
import com.cheesecake.mafia.state.buildProtocol
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LiveGameViewModel(
    private val startGameData: StartGameData,
    private val manageGameRepository: ManageGameRepository,
    private val interactiveGameRepository: InteractiveGameRepository,
): ViewModel() {

    companion object {
        const val HISTORY_SIZE = 15
    }

    private val _state = MutableStateFlow(LiveGameData())
    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    private val _undoStack = MutableStateFlow(ArrayDeque<LiveGameData>(HISTORY_SIZE))
    private val _redoStack = MutableStateFlow(ArrayDeque<LiveGameData>(HISTORY_SIZE))
    private val _gameActive = MutableStateFlow(false)

    val state: StateFlow<LiveGameData> get() = _state
    val history: StateFlow<List<HistoryItem>> get() = _history
    val undoStack: StateFlow<ArrayDeque<LiveGameData>> get() = _undoStack
    val redoStack: StateFlow<ArrayDeque<LiveGameData>> get() = _redoStack
    val gameActive: StateFlow<Boolean> get() = _gameActive

    init {
        val startPlayers = startGameData.items.map { item ->
            LivePlayerData(item.player.id, item.number, item.player.name, item.role)
        }
        val speechPlayers = startPlayers.filter { it.isAlive && !it.isClient }
        val startQueue = _state.value.queueStage
            .push(speechPlayers.map { LiveStage.Day.Speech(it.number) })
            .push(LiveStage.Day.Vote())
        addHistoryItem(HistoryItem.Start(getStateId()))
        changeStateAndNext(historyCached = false) { state ->
            state.copy(
                firstSpeechPlayer = speechPlayers.first().number,
                players = startPlayers,
                queueStage = startQueue,
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

    fun saveGameRepository(time: Long, winner: GameFinishResult, onUploaded: (GameData) -> Unit) {
        viewModelScope.launch {
            val data = buildProtocol(
                startGameData = startGameData,
                liveGameData = _state.value,
                finishResult = winner,
                totalTime = time,
            )
            interactiveGameRepository.clearState()
            manageGameRepository.insert(data)
            onUploaded(data)
        }
    }

    fun onTimerChanged(timer: Int, totalTimer: Int) {
        val active = _state.value.stage.isSpeech && _gameActive.value
        interactiveGameRepository.updateTimer(TimerData(timer, totalTimer, active))
    }

    fun changeStateAndNext(
        historyCached: Boolean = false,
        transform: ((LiveGameData) -> LiveGameData)? = null
    ) {
        changeState(cached = historyCached) { oldState ->
            val state = transform?.let { it(oldState) } ?: oldState
            val (queue, currentStage) = state.queueStage.popFirst()
            currentStage?.let {
                return@let if (currentStage is LiveStage.Day.Vote && state.isVoteMissing) {
                    if (state.isVoteMissing) {
                        val players = state.players.map { player -> player.copy(isClient = false) }
                        state.copy(
                            queueStage = queue,
                            stage = LiveStage.Night(),
                            round = state.round + 1,
                            players = players
                        )
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
                        isVoted = true,
                        actions = player.actions + listOf(
                            GameAction(state.round, GameActionType.DayAction.Voted),
                            GameAction(state.round + 1, GameActionType.Dead(DayType.Day))
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
            val newState = state.copyWithAcceptanceNightActions()
            var speechPlayers = newState.players.filter { it.isAlive && !it.isClient }.map { it.number }
            val groups = speechPlayers.groupBy { it <= _state.value.firstSpeechPlayer }
            speechPlayers = groups[false].orEmpty() + groups[true].orEmpty()
            val queue = newState.queueStage
                .push(state.lastKilledPlayers.sorted().map { LiveStage.Day.LastDeathSpeech(it) })
                .push(speechPlayers.map { LiveStage.Day.Speech(it) })
                .push(LiveStage.Day.Vote())
            val firstSpeech = speechPlayers.firstOrNull() ?: 0
            newState.copy(queueStage = queue, firstSpeechPlayer = firstSpeech)
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
        val transform: (LiveGameData) -> LiveGameData = { state ->
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

    private fun LiveGameData.copyWithAcceptanceNightActions(): LiveGameData {
        val players = players.toMutableList()
        nightActions.forEach { (nightAction, number) ->
            val index = players.indexOfFirst { it.number == number }
            val isKilled = number in lastKilledPlayers
            if (index != -1) {
                val player = players[index]
                players[index] = player.copy(
                    actions = player.actions
                        + listOf(GameAction(round, nightAction)) +
                        if (isKilled) listOf(GameAction(round, GameActionType.Dead(DayType.Night))) else emptyList(),
                    isClient = player.number == lastClientPlayer,
                    isKilled = player.isKilled || isKilled,
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
        transform: (LiveGameData) -> LiveGameData
    ) {
        val oldState = _state.value
        val newState = transform(oldState).incStateId()
        _state.value = newState
        interactiveGameRepository.saveState(InteractiveScreenState.LiveGame(newState))
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

    private fun LiveGameData.incStateId(): LiveGameData {
        return copy(id = id + 1)
    }

    private fun getStateId(): Int {
        return _state.value.id
    }
}