package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.state.GameAction
import com.cheesecake.mafia.state.GameActionType
import com.cheesecake.mafia.state.GamePlayerItemState
import com.cheesecake.mafia.state.GamePlayerRole
import com.cheesecake.mafia.state.GameStageState
import com.cheesecake.mafia.state.NewGamePlayerItem
import com.cheesecake.mafia.state.StageAction
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LiveGameStandingViewModel(
    players: List<NewGamePlayerItem>,
): ViewModel() {

    private val _gameActive = MutableStateFlow(false)
    private val _playerItems = MutableStateFlow(players.map { item ->
        GamePlayerItemState(item.player.id, item.number, item.player.name, item.role)
    })
    private val _showRoles = MutableStateFlow(true)
    private val _stageState = MutableStateFlow(GameStageState(0, StageAction.Start))
    private val _stageActionQueue = MutableStateFlow(ArrayDeque<StageAction>(30))
    private val _voteCandidates = MutableStateFlow<List<Int>>(emptyList())
    private val _killedPlayers = MutableStateFlow<List<Int>>(emptyList())
    private val _clientChosenPlayer = MutableStateFlow<Int?>(null)
    private val _nightAction = MutableStateFlow<Map<GameActionType.NightActon, Int>>(mapOf())
    private val _deletePlayerCandidates = MutableStateFlow<List<Int>>(emptyList())

    val gameActive: StateFlow<Boolean> get() = _gameActive
    val playerItems: StateFlow<List<GamePlayerItemState>> get() = _playerItems
    val showRoles: StateFlow<Boolean> get() = _showRoles
    val stageState: StateFlow<GameStageState> get() = _stageState
    val voteCandidates: StateFlow<List<Int>> get() = _voteCandidates
    val killedPlayers: StateFlow<List<Int>> get() = _killedPlayers
    val clientChosenPlayer: StateFlow<Int?> get() = _clientChosenPlayer
    val deletePlayerCandidates: StateFlow<List<Int>> get() = _deletePlayerCandidates

    init {
        val alivePlayers = _playerItems.value.filter { it.isAlive }
        changeActionQueue { queue ->
            queue.addAll(alivePlayers.map { StageAction.Day.Speech(it.number) })
            queue.add(StageAction.Day.Vote())
            queue
        }
        nextStage()
    }

    fun startOrResumeGame() {
        _gameActive.value = true
    }

    fun pauseGame() {
        _gameActive.value = false
    }

    fun stopGame(time: Int) {
        _gameActive.value = false
    }

    fun changeShowRolesState(showRoles: Boolean) {
        _showRoles.value = showRoles
    }

    fun nextStage() {
        var stageAction = _stageActionQueue.value.removeFirstOrNull() ?: return
        var stageStageIndex = _stageState.value.count
        if (stageAction is StageAction.Day.Vote) {
            if (_voteCandidates.value.isEmpty()) {
                changeActionQueue { queue -> queue.add(StageAction.Night()); queue }
                nextStage()
                return
            } else if (!stageAction.reVote) {
                stageAction = stageAction.copy(
                    candidates = _voteCandidates.value,
                    totalVotes = _playerItems.value.filter { it.isAlive }.size
                )
            }
        } else if (stageAction is StageAction.Night) {
            stageStageIndex += 1
            changeAllItems { it.copy(isClient = false) }
            _voteCandidates.value = emptyList()
        }
        _stageState.value = _stageState.value.copy(
            stageAction = stageAction,
            count = stageStageIndex
        )
    }

    fun addVotedCandidate(playerNumber: Int) {
        if (!_voteCandidates.value.contains(playerNumber)) {
            _voteCandidates.value += listOf(playerNumber)
        }
    }

    fun reVotePlayers(votedPlayers: List<Int>) {
        changeActionQueue { queue ->
            queue.addAll(votedPlayers.map { StageAction.Day.Speech(it, candidateForElimination = true) })
            queue.add(StageAction.Day.Vote(
                candidates = votedPlayers,
                totalVotes = _playerItems.value.filter { it.isAlive }.size,
                reVote = true
            ))
            queue
        }
        nextStage()
    }

    fun votePlayers(votedPlayers: List<Int>) {
        changeActionQueue { queue ->
            queue.addAll(votedPlayers.map { StageAction.Day.LastVotedSpeech(it) })
            queue.add(StageAction.Night())
            queue
        }
        changeItems(votedPlayers) { player ->
            player.copy(
                isAlive = false,
                actions = player.actions + listOf(
                    GameAction(stageState.value.count, GameActionType.DayAction.Voted)
                )
            )
        }
        nextStage()
    }

    fun getNightGameActions(onlyActive: Boolean = false): List<GameActionType.NightActon> {
        val playersRoles = _playerItems.value.filter { it.isAlive || !onlyActive }.map { it.role }
        return GameActionType.NightActon.activeRoles(playersRoles)
    }

    fun changeNightAction(actions: Map<GameActionType.NightActon, Int>) {
        val roles = _playerItems.value.associateBy(keySelector = { it.role }) { it.number }
        val mafiaKilling = actions[GameActionType.NightActon.MafiaKilling]
        val maniacKilling = actions[GameActionType.NightActon.ManiacKilling]
        var clientChosen = actions[GameActionType.NightActon.ClientChoose]
        val doctorSaving = actions[GameActionType.NightActon.Doctor]
        val killedPlayers = if (
            roles[GamePlayerRole.Red.Whore] == mafiaKilling ||
            roles[GamePlayerRole.Red.Whore] == maniacKilling
        ) {
            listOfNotNull(mafiaKilling, maniacKilling, clientChosen)
                .filterNot { number -> doctorSaving == number }
                .also { clientChosen = null }
        } else {
            listOfNotNull(mafiaKilling, maniacKilling)
                .filterNot { number -> doctorSaving == number || clientChosen == number }
        }
        _killedPlayers.value = killedPlayers
        _clientChosenPlayer.value = clientChosen
        _nightAction.value = actions
    }

    fun acceptNightActions() {
        val dayIndex = _stageState.value.count
        _nightAction.value.forEach { (action, number) ->
            changeItem(number) { state ->
                state.copy(
                    actions = state.actions + listOf(GameAction(dayIndex, action)),
                    isAlive = state.isAlive && !_killedPlayers.value.contains(number),
                    isClient = action == GameActionType.NightActon.ClientChoose,
                )
            }
        }
        val speechPlayers = _playerItems.value.filter { it.isAlive && !it.isClient }
        changeActionQueue { queue ->
            queue.addAll(_killedPlayers.value.map { StageAction.Day.LastDeathSpeech(it) })
            queue.addAll(speechPlayers.map { StageAction.Day.Speech(it.number) })
            queue.add(StageAction.Day.Vote())
            queue
        }
        nextStage()
    }

    fun changeFoulsCount(playerNumber: Int, fouls: Int) {
        changeItem(playerNumber) { player -> player.copy(fouls = fouls) }
        _deletePlayerCandidates.value = _playerItems.value
            .filter { !it.isDeleted && it.fouls == 4 }
            .map { it.number }
    }

    fun acceptDeletePlayers(skipToNight: Boolean) {
        val numbers = _deletePlayerCandidates.value.takeIf { it.isNotEmpty() } ?: return
        changeItems(numbers) { players ->
            players.copy(
                isAlive = false,
                isDeleted = true,
                isClient = false,
                fouls = 4,
                actions = players.actions + listOf(
                    GameAction(stageState.value.count, GameActionType.DayAction.Deleted)
                )
            )
        }
        if (skipToNight) {
            changeActionQueue { queue ->
                queue.clear()
                queue.add(StageAction.Night())
                queue
            }
            nextStage()
        }
    }

    private fun changeActionQueue(transform: (queue: ArrayDeque<StageAction>) -> ArrayDeque<StageAction>) {
        _stageActionQueue.value = transform(_stageActionQueue.value)
    }

    private fun changeAllItems(transform: (GamePlayerItemState) -> GamePlayerItemState) {
        val mutableList = _playerItems.value.toMutableList()
        mutableList.forEachIndexed { index, player ->
            mutableList[index] = transform(player)
        }
        _playerItems.value = mutableList.toList()
    }

    private fun changeItems(numbers: List<Int>, transform: (GamePlayerItemState) -> GamePlayerItemState) {
        val mutableList = _playerItems.value.toMutableList()
        numbers.forEach { number ->
            val index = mutableList.indexOfFirst { number == it.number }.takeIf { it != -1 } ?: return
            mutableList[index] = transform(mutableList[index])
        }
        _playerItems.value = mutableList.toList()
    }

    private fun changeItem(number: Int, transform: (GamePlayerItemState) -> GamePlayerItemState) {
        val mutableList = _playerItems.value.toMutableList()
        val index = mutableList.indexOfFirst { it.number == number }.takeIf { it != -1 } ?: return
        mutableList[index] = transform(mutableList[index])
        _playerItems.value = mutableList.toList()
    }
}