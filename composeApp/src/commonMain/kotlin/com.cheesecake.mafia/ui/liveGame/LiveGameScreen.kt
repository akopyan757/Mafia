package com.cheesecake.mafia.ui.liveGame

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.GreyLight
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.WhiteLight
import com.cheesecake.mafia.common.imageResources
import com.cheesecake.mafia.components.liveGame.LiveGameComponent
import com.cheesecake.mafia.state.GameAction
import com.cheesecake.mafia.state.GameActionType
import com.cheesecake.mafia.state.GamePlayerItemState
import com.cheesecake.mafia.state.GameStageState
import com.cheesecake.mafia.state.GameStandingState
import com.cheesecake.mafia.state.GameStatus
import com.cheesecake.mafia.state.StageAction
import com.cheesecake.mafia.ui.GameStanding
import com.cheesecake.mafia.ui.candidateSpeechTimeSeconds
import com.cheesecake.mafia.ui.liveGame.widgets.LiveDeletePlayerWidget
import com.cheesecake.mafia.ui.liveGame.widgets.LiveGameTimer
import com.cheesecake.mafia.ui.liveGame.widgets.LiveGameVoteCandidatesWidget
import com.cheesecake.mafia.ui.liveGame.widgets.LiveNightWidget
import com.cheesecake.mafia.ui.liveGame.widgets.LiveSpeechPlayerTimerWidget
import com.cheesecake.mafia.ui.liveGame.widgets.LiveVoteWidget
import com.cheesecake.mafia.ui.playerSpeechTimeSeconds
import com.cheesecake.mafia.viewModel.LiveGameStandingViewModel
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory


@Composable
fun LiveGameScreen(component: LiveGameComponent) {
    val players by component.model.subscribeAsState()
    val viewModel = getViewModel(
        key = "live-standing",
        factory = viewModelFactory { LiveGameStandingViewModel(players.model) }
    )
    LiveGameScreen(viewModel, onFinishGame = component::onFinishGameClicked)
}

@Composable
fun LiveGameScreen(
    viewModel: LiveGameStandingViewModel,
    onFinishGame: () -> Unit,
) {
    val gameActive by viewModel.gameActive.collectAsState()
    val items by viewModel.playerItems.collectAsState()
    val showRoles by viewModel.showRoles.collectAsState()
    val stageState by viewModel.stageState.collectAsState()
    val candidates by viewModel.voteCandidates.collectAsState()
    val killedPlayers by viewModel.killedPlayers.collectAsState()
    val clientChosenPlayer by viewModel.clientChosenPlayer.collectAsState()
    val deletePlayersCandidates by viewModel.deletePlayerCandidates.collectAsState()
    var showOnlyAlive by remember { mutableStateOf(false) }
    var actionSelections by remember { mutableStateOf(mapOf<GameActionType.NightActon, Int>()) }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LiveGameStanding(
                modifier = Modifier.wrapContentWidth(),
                items = items,
                showRoles = showRoles,
                showOnlyAlive = showOnlyAlive,
                stageState = stageState,
                voteCandidates = candidates,
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
                LiveGameTimer(
                    modifier = Modifier.width(150.dp),
                    active = gameActive,
                    onPauseGame = viewModel::pauseGame,
                    onStopGame = {
                        viewModel.stopGame(it)
                        onFinishGame()
                    },
                )
                SpeechStateWidget(
                    stageAction = stageState.stageAction,
                    gameActive = gameActive,
                    candidates = candidates,
                    onFinish = { viewModel.nextStage() },
                )
                if (stageState.stageAction is StageAction.Day.Vote) {
                    LiveVoteWidget(
                        state = stageState.stageAction as StageAction.Day.Vote,
                        onFinish = { viewModel.votePlayers(it) },
                        onRepeatSpeech = { viewModel.reVotePlayers(it) },
                    )
                }
                if (stageState.stageAction is StageAction.Night ||
                    stageState.stageAction is StageAction.Day.LastDeathSpeech) {
                    LiveNightWidget(
                        allActions = viewModel.getNightGameActions(),
                        killedPlayers = killedPlayers,
                        clientChosen = clientChosenPlayer,
                        onFinish = { viewModel.acceptNightActions() },
                    )
                }
                Column(
                    modifier = Modifier.width(280.dp).padding(end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LiveGameRolesWidget(
                        modifier = Modifier.fillMaxWidth(),
                        showRoles = showRoles,
                        onShowRolesChanged = { showRoles -> viewModel.changeShowRolesState(showRoles) },
                    )
                    LiveGameAliveWidget(
                        modifier = Modifier.fillMaxWidth(),
                        showOnlyAlive = showOnlyAlive,
                        onShowOnlyAliveChanged = { showOnlyAlive = it },
                    )
                    if (deletePlayersCandidates.isNotEmpty()) {
                        LiveDeletePlayerWidget(
                            modifier = Modifier.fillMaxWidth(),
                            playerNumbers = deletePlayersCandidates,
                            isDayStage = stageState.stageAction is StageAction.Day,
                            onAccept = { viewModel.acceptDeletePlayers(it) },
                        )
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
    items: List<GamePlayerItemState> = emptyList(),
    showRoles: Boolean = true,
    showOnlyAlive: Boolean = false,
    stageState: GameStageState,
    onFoulsChanged: (number: Int, fouls: Int) -> Unit,
    onPutOnVote: (number: Int) -> Unit = {},
    voteCandidates: List<Int> = emptyList(),
    nightActions: List<GameActionType.NightActon> = emptyList(),
    onChangeAction: (Map<GameActionType.NightActon, Int>) -> Unit = {}
) {
    val alivePlayers = items.filter { it.isAlive }
    val players = if (showOnlyAlive) alivePlayers else items
    val nightActionsChecks = remember(stageState, nightActions.size, items.size) {
        mutableStateMapOf(
            *Array(players.size) { index ->
                players[index].number to nightActions.map { SelectedNightGameAction(it, false) }
            }
        )
    }
    val actionSelections by remember(stageState, nightActions.size, items.size) {
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
            stage = stageState,
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
                stageState = stageState,
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
                    checkedTrackColor = GreyLight,
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
                    checkedTrackColor = GreyLight,
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
    stageAction: StageAction,
    candidates: List<Int> = emptyList(),
    onFinish: () -> Unit = {},
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when (stageAction) {
            is StageAction.Day.LastVotedSpeech -> stageAction.Widget(modifier, gameActive, onFinish)
            is StageAction.Day.LastDeathSpeech -> stageAction.Widget(modifier, gameActive, onFinish)
            is StageAction.Day.Speech -> stageAction.Widget(modifier, gameActive, onFinish)
            else -> {}
        }

        if (candidates.isNotEmpty() && stageAction is StageAction.Day.Speech) {
            LiveGameVoteCandidatesWidget(
                numbers = candidates,
            )
        }
    }
}

@Composable
fun StageAction.Day.LastVotedSpeech.Widget(
    modifier: Modifier = Modifier,
    gameActive: Boolean = false,
    onFinish: () -> Unit = {},
) {
    LiveSpeechPlayerTimerWidget(
        modifier = modifier,
        gameActive = gameActive,
        title = "Последнее слово заголосованного",
        playerNumber = playerNumber,
        seconds = playerSpeechTimeSeconds,
        onFinish = onFinish
    )
}

@Composable
fun StageAction.Day.LastDeathSpeech.Widget(
    modifier: Modifier = Modifier,
    gameActive: Boolean = false,
    onFinish: () -> Unit = {},
) {
    LiveSpeechPlayerTimerWidget(
        modifier = modifier,
        gameActive = gameActive,
        title = "Последнее слово умершего",
        playerNumber = playerNumber,
        seconds = playerSpeechTimeSeconds,
        onFinish = onFinish
    )
}

@Composable
fun StageAction.Day.Speech.Widget(
    modifier: Modifier = Modifier,
    gameActive: Boolean = false,
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
        onFinish = onFinish
    )
}