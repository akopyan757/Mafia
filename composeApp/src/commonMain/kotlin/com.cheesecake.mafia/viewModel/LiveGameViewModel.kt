package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.common.changeItem
import com.cheesecake.mafia.common.changeItems
import com.cheesecake.mafia.data.ApiResult
import com.cheesecake.mafia.data.GameAction
import com.cheesecake.mafia.data.GameActionType
import com.cheesecake.mafia.data.InteractiveScreenState
import com.cheesecake.mafia.data.LiveGameData
import com.cheesecake.mafia.data.LivePlayerData
import com.cheesecake.mafia.data.LiveStage
import com.cheesecake.mafia.data.onError
import com.cheesecake.mafia.data.onSuccess
import com.cheesecake.mafia.repository.InteractiveGameRepository
import com.cheesecake.mafia.repository.LiveGameRepository
import com.cheesecake.mafia.repository.ManageGameRepository
import com.cheesecake.mafia.state.HistoryItem
import com.cheesecake.mafia.state.SelectPlayerState
import com.cheesecake.mafia.state.StartData
import com.cheesecake.mafia.state.buildProtocol
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class LiveGameViewModel(
    startData: StartData,
    private val manageGameRepository: ManageGameRepository,
    private val liveGameRepository: LiveGameRepository,
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
    private val _showInteractive = MutableStateFlow(true)
    private val _showInteractiveCandidates = MutableStateFlow(true)
    private val _showInteractiveTimer = MutableStateFlow(true)
    private val _timer = MutableStateFlow(0L)
    private val _errorMessage = MutableStateFlow("")

    val state: StateFlow<LiveGameData> get() = _state
    val history: StateFlow<List<HistoryItem>> get() = _history
    val undoStack: StateFlow<ArrayDeque<LiveGameData>> get() = _undoStack
    val redoStack: StateFlow<ArrayDeque<LiveGameData>> get() = _redoStack
    val gameActive: StateFlow<Boolean> get() = _gameActive
    val showInteractive: StateFlow<Boolean> get() = _showInteractive
    val showInteractiveCandidates: StateFlow<Boolean> get() = _showInteractiveCandidates
    val showInteractiveTimer: StateFlow<Boolean> get() = _showInteractiveTimer
    val timer: StateFlow<Long> = _timer
    val errorMessage: StateFlow<String> = _errorMessage

    init {
        when (startData) {
            is StartData.NewGame -> initNewGame(startData)
            is StartData.ResumeExistGame -> resumeExistGame(startData)
        }
    }

    private fun initNewGame(data: StartData.NewGame) {
        val gameId = Random.nextLong()
        val startPlayers = data.items.map { item ->
            LivePlayerData(
                gameId = gameId,
                playerId = item.player.id,
                number = item.number,
                name = item.player.name,
                role = item.role,
                isNewPlayer = item.player is SelectPlayerState.New
            )
        }
        val speechPlayers = startPlayers.filter { it.isAlive && !it.isClient }
        val startQueue = _state.value.queueStage
            .push(speechPlayers.map { LiveStage.Day.Speech(it.number, false) })
            .push(LiveStage.Day.Vote(false))
        addHistoryItem(HistoryItem.Start(getStateId()))
        changeStateAndNext(historyCached = false) { state ->
            print("start game: ${gameId}")
            state.copy(
                gameId = gameId,
                title = data.title,
                date = data.date,
                firstSpeechPlayer = speechPlayers.first().number,
                players = startPlayers,
                queueStage = startQueue,
            )
        }
        initTimer()
    }

    private fun resumeExistGame(existGame: StartData.ResumeExistGame) {
        _state.value = existGame.data.let { data ->
            data.copy(players = data.players.sortedBy { it.number })
        }
        initTimer(existGame.data.totalTime)
    }

    private fun initTimer(value: Long = 0L) {
        CoroutineScope(Dispatchers.Main).launch {
            _timer.value = value
            while (true) {
                delay(1000L)
                if (_gameActive.value) {
                    _timer.value += 1
                }
            }
        }
    }

    fun resetErrorMessage() {
        _errorMessage.value = ""
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

    fun saveGameRepository(onUploaded: (gameId: Long) -> Unit) {
        viewModelScope.launch {
            val data = buildProtocol(_state.value, _timer.value)
            interactiveGameRepository.clearState()
            when (val result = manageGameRepository.insert(data)) {
                is ApiResult.Success -> {
                    liveGameRepository.deleteById(_state.value.gameId)
                    onUploaded(result.data)
                }
                else -> {}
            }
        }
    }

    fun showInteractive(value: Boolean) {
        _showInteractive.value = value
        val settings = interactiveGameRepository.getSettings().copy(showInteractive = value)
        interactiveGameRepository.updateSettings(settings)
    }

    fun showInteractiveTimer(value: Boolean) {
        _showInteractiveTimer.value = value
        val settings = interactiveGameRepository.getSettings().copy(showTimer = value)
        interactiveGameRepository.updateSettings(settings)
    }

    fun showInteractiveCandidates(value: Boolean) {
        _showInteractiveCandidates.value = value
        val settings = interactiveGameRepository.getSettings().copy(showCandidates = value)
        interactiveGameRepository.updateSettings(settings)
    }

    fun onTimerChanged(timer: Int, totalTimer: Int) {
        val settings = interactiveGameRepository.getSettings()
        val newSettings = settings.copy(
            timeValue = timer, timerTotal = totalTimer
        )
        interactiveGameRepository.updateSettings(newSettings)
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
                            stage = LiveStage.Night,
                            round = state.round.inc(),
                            players = players,
                            voteCandidates = emptyList(),
                        )
                    } else {
                        state.copy(
                            queueStage = queue,
                            stage = currentStage,
                        )
                    }
                } else if (currentStage is LiveStage.Night) {
                    state.copy(
                        round = state.round.inc(),
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
                .push(LiveStage.Night)
            state.copy(
                queueStage = queue,
                players = state.players.changeItems(votedPlayers) { player ->
                    player.copy(
                        isAlive = false,
                        isVoted = true,
                        actions = player.actions + listOf(
                            GameAction(state.round, GameActionType.DayAction.Voted),
                            GameAction(state.round.inc(), GameActionType.Dead.Day)
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

    fun acceptNightActions(bestMoves: Map<Int, List<Int>>) {
        val nightActions = _state.value.nightActions
        changeStateAndNext(historyCached = true) { state ->
            val newState = state.copyWithAcceptanceNightActions().mergeBestMove(bestMoves)
            var speechPlayers = newState.players.filter { it.isAlive && !it.isClient }.map { it.number }
            val groups = speechPlayers.groupBy { it <= _state.value.firstSpeechPlayer }
            speechPlayers = groups[false].orEmpty() + groups[true].orEmpty()
            val queue = newState.queueStage
                .push(state.lastKilledPlayers.sorted().map { LiveStage.Day.LastDeathSpeech(it) })
                .push(speechPlayers.map { LiveStage.Day.Speech(it, false) })
                .push(LiveStage.Day.Vote(reVote = false))
            val firstSpeech = speechPlayers.firstOrNull() ?: 0
            newState.copy(
                queueStage = queue,
                firstSpeechPlayer = firstSpeech,
            )
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
                    listOf(LiveStage.Night)
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
                            listOf(GameAction(state.round, GameActionType.Dead.ofDayType(state.stage.dayType))),
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
                HistoryItem.DeletePlayer(playerNumber, _state.value.stage.dayType, getStateId())
            }
        )
    }

    fun stopGame(onStopped: () -> Unit) {
        viewModelScope.launch {
            val state = _state.value
            liveGameRepository.insertOrUpdate(
                state.copy(totalTime = _timer.value)
            )
            onStopped()
        }
    }

    fun deleteGame(onSuccess: () -> Unit) {
        viewModelScope.launch {
            print("delete id = ${_state.value.gameId}")
            liveGameRepository.deleteById(_state.value.gameId).collect { result ->
                result.onSuccess { onSuccess() }
                    .onError { _errorMessage.value = it.message.toString() }
            }
        }
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
                        if (isKilled) listOf(GameAction(round, GameActionType.Dead.Night)) else emptyList(),
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

    private fun LiveGameData.mergeBestMove(bestMoves: Map<Int, List<Int>>): LiveGameData {
        return copy(
            players = players.map { player ->
                if (player.number in bestMoves) {
                    player.copy(bestMove = bestMoves[player.number].orEmpty())
                } else player
            }
        )
    }

    private fun addHistoryItem(historyItem: HistoryItem) {
        _history.value += historyItem
    }

    private fun addHistoryItems(historyItems: List<HistoryItem>) {
        _history.value += historyItems
    }

    private fun LiveGameData.takeTime(): LiveGameData {
        return copy(totalTime = _timer.value)
    }

    private fun changeState(
        cached: Boolean = false,
        transform: (LiveGameData) -> LiveGameData
    ) {
        val oldState = _state.value
        val newState = transform(oldState).takeTime().incStateId()
        _state.value = newState
        viewModelScope.launch {
            liveGameRepository.insertOrUpdate(newState).collect {}
        }
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

    private fun getStateId(): Long {
        return _state.value.id
    }
}