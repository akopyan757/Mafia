package com.cheesecake.mafia.ui.liveGame

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.GrayLight
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.WhiteLight
import com.cheesecake.mafia.common.imageResources
import com.cheesecake.mafia.components.liveGame.LiveGameComponent
import com.cheesecake.mafia.data.GameActionType
import com.cheesecake.mafia.state.GameStandingState
import com.cheesecake.mafia.state.GameStatus
import com.cheesecake.mafia.data.LivePlayerData
import com.cheesecake.mafia.data.LiveStage
import com.cheesecake.mafia.ui.GameStanding
import com.cheesecake.mafia.ui.candidateSpeechTimeSeconds
import com.cheesecake.mafia.ui.liveGame.widgets.LiveDeletePlayerWidget
import com.cheesecake.mafia.ui.liveGame.widgets.LiveGameTimer
import com.cheesecake.mafia.ui.liveGame.widgets.LiveGameVoteCandidatesWidget
import com.cheesecake.mafia.ui.liveGame.widgets.LiveNightWidget
import com.cheesecake.mafia.ui.liveGame.widgets.LiveSpeechPlayerTimerWidget
import com.cheesecake.mafia.ui.liveGame.widgets.LiveVoteWidget
import com.cheesecake.mafia.ui.playerSpeechTimeSeconds
import com.cheesecake.mafia.viewModel.LiveGameViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun LiveGameScreen(component: LiveGameComponent) {
    val players by component.model.subscribeAsState()
    val viewModel = koinInject<LiveGameViewModel> { parametersOf(players.data) }
    LiveGameScreen(viewModel, onFinishGame = component::onFinishGameClicked)
}

@Composable
fun LiveGameScreen(
    viewModel: LiveGameViewModel,
    onFinishGame: (gameId: Long) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val history by viewModel.history.collectAsState()
    val undoStack by viewModel.undoStack.collectAsState()
    val redoStack by viewModel.redoStack.collectAsState()
    val gameActive by viewModel.gameActive.collectAsState()
    var showRoles by remember { mutableStateOf(true) }
    var showOnlyAlive by remember { mutableStateOf(false) }
    var actionSelections by remember { mutableStateOf(mapOf<GameActionType.NightActon, Int>()) }

    Box(Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LiveGameStanding(
                    modifier = Modifier,
                    items = state.players,
                    showRoles = showRoles,
                    showOnlyAlive = showOnlyAlive,
                    round = state.round,
                    stage = state.stage,
                    voteCandidates = state.voteCandidates,
                    onPutOnVote = { number -> viewModel.addVotedCandidate(number) },
                    onFoulsChanged = { number, fouls -> viewModel.changeFoulsCount(number, fouls) },
                    nightActions = viewModel.getNightGameActions(onlyActive = true),
                    onChangeAction = {
                        actionSelections = it
                        viewModel.changeNightAction(actionSelections)
                    }
                )
                Row(
                    modifier = Modifier.wrapContentSize().defaultMinSize(minHeight = 200.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                ) {
                    SpeechStateWidget(
                        modifier = Modifier,
                        stageAction = state.stage,
                        gameActive = gameActive,
                        candidates = state.voteCandidates,
                        onTimerChanged = viewModel::onTimerChanged,
                        onFinish = { viewModel.changeStateAndNext(historyCached = true) },
                    )
                    if (state.stage is LiveStage.Day.Vote) {
                        LiveVoteWidget(
                            state = state.stage as LiveStage.Day.Vote,
                            totalVotes = state.totalVotes,
                            candidates = state.voteCandidates,
                            onFinish = { viewModel.votePlayers(it) },
                            onRepeatSpeech = { viewModel.reVotePlayers(it) },
                        )
                    }
                    if (state.stage is LiveStage.Night) {
                        LiveNightWidget(
                            allActions = viewModel.getNightGameActions(),
                            killedPlayers = state.lastKilledPlayers,
                            clientChosen = state.lastClientPlayer,
                            onFinish = { viewModel.acceptNightActions() },
                        )
                    }
                    Column(
                        modifier = Modifier.width(280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LiveGameRolesWidget(
                            modifier = Modifier.fillMaxWidth(),
                            showRoles = showRoles,
                            onShowRolesChanged = { showRoles = it },
                        )
                        LiveGameAliveWidget(
                            modifier = Modifier.fillMaxWidth(),
                            showOnlyAlive = showOnlyAlive,
                            onShowOnlyAliveChanged = { showOnlyAlive = it },
                        )
                    }
                    val deletePlayersCandidates = state.deleteCandidates
                    if (deletePlayersCandidates.isNotEmpty()) {
                        LiveDeletePlayerWidget(
                            modifier = Modifier.wrapContentWidth(),
                            playerNumbers = deletePlayersCandidates,
                            isDayStage = state.stage is LiveStage.Day,
                            onAccept = { viewModel.acceptDeletePlayers(it) },
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.width(250.dp).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                 val finishResult = state.winner
                 LiveGameTimer(
                    modifier = Modifier.fillMaxWidth(),
                    active = gameActive,
                    finishResult = finishResult,
                    onPauseGame = viewModel::pauseGame,
                    onStopGame = { time ->
                        finishResult?.let {
                            viewModel.saveGameRepository(time, finishResult) { data ->
                                onFinishGame(data.id)
                            }
                        }
                    },
                    redoActive = redoStack.size > 0,
                    undoActive = undoStack.size > 0,
                    onUndo = { viewModel.undoState() }
                 ) { viewModel.redoState() }
                 Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    backgroundColor = White,
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(history) { history ->
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = history.text,
                                style = MaterialTheme.typography.body2.copy(fontSize = 10.sp),
                                color = BlackDark,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
        if (!gameActive) {
            Box(
                Modifier.fillMaxSize()
                    .background(Color.Gray.copy(alpha = 0.5f))
                    .clickable(false) {}
            ) {
                IconButton(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = { viewModel.startOrResumeGame() },
                ) {
                    Icon(
                        painter = imageResources("ic_play_button.xml"),
                        contentDescription = null,
                        tint = BlackDark,
                        modifier = Modifier.size(100.dp).align(Alignment.Center)
                    )
                }
            }
        }
    }
}

data class SelectedNightGameAction(
    val action: GameActionType.NightActon,
    val checked: Boolean,
)

@Composable
fun LiveGameStanding(
    modifier: Modifier = Modifier,
    items: List<LivePlayerData> = emptyList(),
    showRoles: Boolean = true,
    showOnlyAlive: Boolean = false,
    round: Int = 0,
    stage: LiveStage = LiveStage.Start,
    onFoulsChanged: (number: Int, fouls: Int) -> Unit,
    onPutOnVote: (number: Int) -> Unit = {},
    voteCandidates: List<Int> = emptyList(),
    nightActions: List<GameActionType.NightActon> = emptyList(),
    onChangeAction: (Map<GameActionType.NightActon, Int>) -> Unit = {}
) {
    val alivePlayers = items.filter { it.isAlive }
    val players = if (showOnlyAlive) alivePlayers else items
    val nightActionsChecks = remember(stage, nightActions.size, items.size) {
        mutableStateMapOf(
            *Array(players.size) { index ->
                players[index].number to nightActions.map { SelectedNightGameAction(it, false) }
            }
        )
    }
    val actionSelections by remember(stage, nightActions.size, items.size) {
        derivedStateOf {
            hashMapOf<GameActionType.NightActon, Int>().also {
                nightActionsChecks.forEach { (number, actions) ->
                    actions.forEach { (action, checked) ->
                        if (checked) it[action] = number
                    }
                }
            }
        }
    }

    GameStanding(
        modifier = modifier,
        standingState = GameStandingState(
            id = 0,
            status = GameStatus.Live,
            round = round,
            dayType = stage.type,
            isShowRoles = showRoles,
        ),
        itemsCount = players.size,
        itemContent = { position ->
            val item = players[position]
            val actions = nightActionsChecks[item.number] ?: emptyList()
            LiveGameItem(
                modifier = Modifier,
                player = item,
                onFoulsChanged = { fouls -> onFoulsChanged(item.number, fouls) },
                onPutOnVote = { onPutOnVote(item.number) },
                showRoles = showRoles,
                isPutOnVote = voteCandidates.contains(item.number),
                round = round,
                stage = stage,
                checkedActions = actions,
                onActionCheckedChanged = { actionChecks ->
                    nightActionsChecks[item.number] = actionChecks
                    nightActionsChecks.forEach { (number, actions) ->
                        if (number != item.number) {
                            nightActionsChecks[number] = actions.uncheckedFrom(actionChecks)
                        }
                    }
                    onChangeAction(actionSelections)
                },
            )
        }
    )
}

private fun List<SelectedNightGameAction>.uncheckedFrom(
    others: List<SelectedNightGameAction>
): List<SelectedNightGameAction> {
    if (size != others.size) return this
    return mapIndexed { index, action ->
        if (others[index].checked && action.checked) {
            action.copy(checked = false)
        } else action
    }
}

@Composable
fun LiveGameRolesWidget(
    modifier: Modifier = Modifier,
    showRoles: Boolean,
    onShowRolesChanged: (Boolean) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = White,
    ) {
        Row(
            modifier = modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Показать роли",
                style = MaterialTheme.typography.body1,
            )
            Switch(
                checked = showRoles,
                onCheckedChange = onShowRolesChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = BlackDark,
                    checkedTrackColor = GrayLight,
                    uncheckedThumbColor = WhiteLight,
                    uncheckedTrackColor = White,
                )
            )
        }
    }
}
@Composable
fun LiveGameAliveWidget(
    modifier: Modifier = Modifier,
    showOnlyAlive: Boolean,
    onShowOnlyAliveChanged: (Boolean) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = White,
    ) {
        Row(
            modifier = modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Показать только живых",
                style = MaterialTheme.typography.body1,
            )
            Switch(
                checked = showOnlyAlive,
                onCheckedChange = onShowOnlyAliveChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = BlackDark,
                    checkedTrackColor = GrayLight,
                    uncheckedThumbColor = WhiteLight,
                    uncheckedTrackColor = White,
                )
            )
        }
    }
}

@Composable
fun SpeechStateWidget(
    modifier: Modifier = Modifier,
    gameActive: Boolean = false,
    stageAction: LiveStage,
    candidates: List<Int> = emptyList(),
    onTimerChanged: (time: Int, totalTimer: Int) -> Unit = { _, _ -> },
    onFinish: () -> Unit = {},
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when (stageAction) {
            is LiveStage.Day.LastVotedSpeech -> stageAction.Widget(modifier, gameActive, onTimerChanged, onFinish)
            is LiveStage.Day.LastDeathSpeech -> stageAction.Widget(modifier, gameActive, onTimerChanged, onFinish)
            is LiveStage.Day.Speech -> stageAction.Widget(modifier, gameActive, onTimerChanged, onFinish)
            else -> {}
        }

        if (candidates.isNotEmpty() && stageAction is LiveStage.Day.Speech) {
            LiveGameVoteCandidatesWidget(
                numbers = candidates,
            )
        }
    }
}

@Composable
fun LiveStage.Day.LastVotedSpeech.Widget(
    modifier: Modifier = Modifier,
    gameActive: Boolean = false,
    onTimerChanged: (time: Int, totalTimer: Int) -> Unit = { _, _ -> },
    onFinish: () -> Unit = {},
) {
    LiveSpeechPlayerTimerWidget(
        modifier = modifier,
        gameActive = gameActive,
        title = "Последнее слово заголосованного",
        playerNumber = playerNumber,
        seconds = playerSpeechTimeSeconds,
        onTimerChanged = onTimerChanged,
        onFinish = onFinish,
    )
}

@Composable
fun LiveStage.Day.LastDeathSpeech.Widget(
    modifier: Modifier = Modifier,
    gameActive: Boolean = false,
    onTimerChanged: (time: Int, totalTimer: Int) -> Unit = { _, _ -> },
    onFinish: () -> Unit = {},
) {
    LiveSpeechPlayerTimerWidget(
        modifier = modifier,
        gameActive = gameActive,
        title = "Последнее слово умершего",
        playerNumber = playerNumber,
        seconds = playerSpeechTimeSeconds,
        onTimerChanged = onTimerChanged,
        onFinish = onFinish,
    )
}

@Composable
fun LiveStage.Day.Speech.Widget(
    modifier: Modifier = Modifier,
    gameActive: Boolean = false,
    onTimerChanged: (time: Int, totalTimer: Int) -> Unit = { _, _ -> },
    onFinish: () -> Unit = {},
) {
    val time = if (candidateForElimination) candidateSpeechTimeSeconds else playerSpeechTimeSeconds
    val title = if (candidateForElimination) "Оправдательное слово" else ""
    LiveSpeechPlayerTimerWidget(
        modifier = modifier,
        gameActive = gameActive,
        title = title,
        playerNumber = playerNumber,
        seconds = time,
        onTimerChanged = onTimerChanged,
        onFinish = onFinish,
    )
}