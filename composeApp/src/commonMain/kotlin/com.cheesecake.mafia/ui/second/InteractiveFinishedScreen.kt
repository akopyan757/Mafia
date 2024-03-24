package com.cheesecake.mafia.ui.second

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cheesecake.mafia.common.Blue
import com.cheesecake.mafia.common.GrayLight
import com.cheesecake.mafia.common.LogoTint
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.imageResources
import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.GameFinishResult
import com.cheesecake.mafia.data.GamePlayerData
import com.cheesecake.mafia.data.TimerData
import com.cheesecake.mafia.state.primaryColor
import com.cheesecake.mafia.state.secondaryColor
import com.cheesecake.mafia.ui.custom.GameRoleItem
import com.cheesecake.mafia.ui.custom.Grid

@Composable
fun InteractiveFinishedScreen(gameData: GameData) {
    val playersCount = gameData.players.size
    val (top, right, left, bottom) = createSquare(playersCount)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        Image(
            painter = imageResources("background.xml"),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Grid(
            columns = top.size,
            modifier = Modifier.fillMaxSize().padding(8.dp),
        ) {
            top.forEach { index ->
                val data = gameData.players.getOrNull(index)
                PlayerItem(
                    modifier = Modifier.span(columns = 1, rows = 1),
                    playerData = data,
                )
            }
            Column(modifier = Modifier.span(columns = 1, rows = left.size)) {
                left.forEach { index ->
                    val data = gameData.players.getOrNull(index)
                    PlayerItem(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        playerData = data
                    )
                }
            }
            Box(
                modifier = Modifier.span(columns = top.size - 2, rows = left.size),
            ) {
                val text = when (gameData.finishResult) {
                    GameFinishResult.BlackWin -> "Победа мафии"
                    GameFinishResult.RedWin -> "Победа мирного города"
                    GameFinishResult.WhiteWin -> "Победа маньяка"
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.h3,
                    color = White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Column(modifier = Modifier.span(columns = 1, rows = right.size)) {
                right.forEach { index ->
                    val data = gameData.players.getOrNull(index)
                    PlayerItem(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        playerData = data,
                    )
                }
            }
            bottom.forEach { index ->
                val data = gameData.players.getOrNull(index)
                PlayerItem(
                    modifier = Modifier.span(columns = 1, rows = 1),
                    playerData = data,
                )
            }
        }
    }
}

@Composable
private fun PlayerItem(
    modifier: Modifier = Modifier,
    playerData: GamePlayerData?,
) {
    Box(modifier) {
        Card(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            shape = RoundedCornerShape(4.dp),
            backgroundColor = GrayLight.copy(alpha = 0.5f),
        ) {
            if (playerData != null) {
                val role = playerData.role
                Column(
                    Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.weight(0.3F))
                    PlayerNumberWidget(
                        modifier = Modifier.size(70.dp),
                        number = playerData.number,
                    )
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = playerData.name,
                        style = MaterialTheme.typography.h5.copy(fontSize = 30.sp),
                        color = White
                    )
                    Spacer(Modifier.weight(0.2F))
                    Card(
                        modifier = Modifier.fillMaxWidth(0.6f),
                        shape = RoundedCornerShape(8.dp),
                        backgroundColor = role.primaryColor(),
                    ) {
                        Row(
                            modifier = modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (role.iconRes.isNotEmpty()) {
                                Icon(
                                    painter = imageResources(role.iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp).padding(start = 16.dp),
                                    tint = role.secondaryColor(),
                                )
                            }
                            Text(
                                text = role.name,
                                style = MaterialTheme.typography.h6,
                                textAlign = TextAlign.Center,
                                color = role.secondaryColor(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(Modifier.weight(0.3F))
                }
            } else {
                Image(
                    painter = imageResources("logo_gt.xml"),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    colorFilter = ColorFilter.tint(LogoTint),
                    modifier = Modifier.align(Alignment.Center).fillMaxHeight(0.8f).padding(8.dp)
                )
            }
        }
    }
}

