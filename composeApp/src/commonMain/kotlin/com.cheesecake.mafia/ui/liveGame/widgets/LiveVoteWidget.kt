package com.cheesecake.mafia.ui.liveGame.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.VoteColorChosen
import com.cheesecake.mafia.common.VoteColorDisabled
import com.cheesecake.mafia.common.VoteColorEnable
import com.cheesecake.mafia.common.VoteColorZero
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.imageResources
import com.cheesecake.mafia.state.StageAction

private fun SnapshotStateMap<Int, Int>.votedPlayers(): List<Int> {
    val maxVotesValue = values.maxOrNull() ?: 0
    return toList()
        .filter { (_, count) -> count == maxVotesValue }
        .map { (number, _) -> number }
}

private val firstColumnWidth = 60.dp

@Composable
fun LiveVoteWidget(
    modifier: Modifier = Modifier,
    state: StageAction.Day.Vote,
    onRepeatSpeech: (votedPlayers: List<Int>) -> Unit = {},
    onFinish: (votedPlayers: List<Int>) -> Unit = {},
) {
    var activeVotesCount by remember { mutableStateOf(state.totalVotes) }
    val votes = remember { mutableStateMapOf<Int, Int>() }
    val votedPlayers by derivedStateOf { votes.votedPlayers() }
    val repeatSpeech by derivedStateOf {
        if (state.reVote)
            1 < votedPlayers.size && votedPlayers.size < state.candidates.size
        else
            1 < votedPlayers.size
    }
    var eliminationAgreeCount by remember { mutableStateOf(-1) }
    val isEliminationQuestion by derivedStateOf { !repeatSpeech && activeVotesCount == 0 && votedPlayers.size > 1 }

    Card(
        modifier = modifier.wrapContentSize(),
        backgroundColor = White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            state.candidates.forEach { number ->
                VotesSelectItem(
                    label = number.toString(),
                    votes = votes[number] ?: 0,
                    totalVotesCount = state.totalVotes,
                    activeVotesCount = activeVotesCount + (votes[number] ?: 0),
                    onVotesSelected = { count ->
                        votes[number] = count
                        activeVotesCount = state.totalVotes - votes.values.sum()
                    }
                )
            }

            if (isEliminationQuestion) {
                Text(
                    text = "Кто за то, чтобы игроки покинули игру?",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(start = firstColumnWidth, top = 16.dp)
                )
                VotesSelectItem(
                    modifier = Modifier.padding(vertical = 4.dp),
                    label = "За",
                    votes = eliminationAgreeCount,
                    totalVotesCount = state.totalVotes,
                    onVotesSelected = { count -> eliminationAgreeCount = count }
                )
            }

            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val votedPlayersValue = votedPlayers.joinToString(separator = ", ") { it.toString() }
                val acceptDescriptionText = if (isEliminationQuestion) {
                    if (eliminationAgreeCount > state.totalVotes / 2) {
                        "Покидают игроки $votedPlayersValue"
                    } else {
                        "Все игроки остаются"
                    }
                } else if (repeatSpeech) {
                    "Переголосование игроков $votedPlayersValue"
                } else {
                    "Покидают игроки $votedPlayersValue"
                }
                Button(
                    modifier = Modifier.width(firstColumnWidth).padding(end = 8.dp),
                    enabled = activeVotesCount == 0,
                    colors = ButtonDefaults.buttonColors(backgroundColor = BlackDark),
                    onClick = {
                        if (isEliminationQuestion) {
                            if (eliminationAgreeCount > state.totalVotes / 2) {
                                onFinish(votedPlayers)
                            } else {
                                onFinish(emptyList())
                            }
                        } else if (repeatSpeech) {
                            onRepeatSpeech(votedPlayers)
                        } else {
                            onFinish(votedPlayers)
                        }
                    },
                ) {
                    Icon(
                        painter = imageResources("ic_check_circle.xml"),
                        contentDescription = null,
                        tint = White,
                    )
                }
                if (activeVotesCount == 0) {
                    Text(
                        text = acceptDescriptionText,
                        style = MaterialTheme.typography.body1,
                        color = BlackDark,
                    )
                }
            }
        }
    }
}

@Composable
fun VotesSelectItem(
    modifier: Modifier = Modifier,
    label: String,
    votes: Int,
    totalVotesCount: Int,
    activeVotesCount: Int = totalVotesCount,
    onVotesSelected: (Int) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(firstColumnWidth)
                .padding(end = 8.dp)
                .align(Alignment.CenterVertically),
        )
        (0..totalVotesCount).forEach { boxIndex ->
            val isClickEnabled = boxIndex <= activeVotesCount || boxIndex <= votes
            val color = when {
                boxIndex == 0 -> VoteColorZero
                boxIndex <= votes -> VoteColorChosen
                isClickEnabled -> VoteColorEnable
                else -> VoteColorDisabled
            }
            Box(
                modifier = modifier.size(30.dp)
                    .background(color = color)
                    .clip(RoundedCornerShape(2.dp))
                    .clickable {
                        if (isClickEnabled) {
                            onVotesSelected(boxIndex)
                        }
                    }
            ) {
                Text(
                    text = boxIndex.toString(),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.body2,
                    color = BlackDark.copy(alpha = 0.5f)
                )
            }
        }
    }
}