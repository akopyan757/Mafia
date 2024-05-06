package com.cheesecake.mafia.ui.finishedGame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.LogoTint
import com.cheesecake.mafia.common.Red
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.WhiteLight
import com.cheesecake.mafia.common.imageResources
import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.GameFinishResult
import com.cheesecake.mafia.data.GamePlayerRole
import com.cheesecake.mafia.data.russianText

@Composable
fun CaptureFinishedScreen(
    modifier: Modifier = Modifier,
    gameData: GameData,
) {
    val localDate = gameData.date.split(".")
    val day = localDate[0].removePrefix("0")
    val month = when(localDate[1]) {
        "01" -> "ЯНВ"
        "02" -> "ФЕВ"
        "03" -> "МАР"
        "04" -> "АПР"
        "05" -> "МАЯ"
        "06" -> "ИЮНЯ"
        "07" -> "ИЮЛЯ"
        "08" -> "АВГ"
        "09" -> "СЕН"
        "10" -> "ОКТ"
        "11" -> "НОЯ"
        else -> "ДЕК"
    }
    val winnerColor = when (gameData.finishResult) {
        GameFinishResult.BlackWin -> Color(0x4C0D0D13)
        GameFinishResult.RedWin -> Red
        else -> WhiteLight
    }
    val winnerTextColor = when (gameData.finishResult) {
        GameFinishResult.BlackWin, GameFinishResult.RedWin -> White
        else -> BlackDark
    }
    val winnerText = when (gameData.finishResult) {
        GameFinishResult.BlackWin -> "ЧЕРНЫХ"
        GameFinishResult.RedWin -> "КРАСНЫХ"
        else -> "МАНЬЯКА"
    }
    Box(
        modifier.width(1080.pxToDp()).height(1920.pxToDp())
    ) {
        Image(
            painter = imageResources("ic_inst_background.xml"),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(day, color = Color.White, fontSize = 96.pxToSp(), style = MaterialTheme.typography.body1)
                    Text(month, color = Color.White, fontSize = 40.pxToSp(), style = MaterialTheme.typography.body1)
                }
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .height(160.pxToDp())
                        .clip(RoundedCornerShape(16.dp))
                        .weight(1f)
                        .background(winnerColor),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "ПОБЕДА",
                        textAlign = TextAlign.Center,
                        color = winnerTextColor,
                        fontSize = 32.pxToSp(),
                        style = MaterialTheme.typography.body1,
                    )
                    Text(
                        text = winnerText,
                        textAlign = TextAlign.Center,
                        color = winnerTextColor,
                        fontSize = 64.pxToSp(),
                        style = MaterialTheme.typography.body1,
                    )
                }
                Icon(
                    modifier = Modifier.size(200.pxToDp()),
                    painter = imageResources("logo_gt.xml"),
                    contentDescription = null,
                    tint = LogoTint
                )
            }

            Text(
                "Ведущая: Г-жа Сова",
                style = MaterialTheme.typography.body1,
                fontSize = 24.sp,
                modifier = Modifier.padding(vertical = 8.dp),
                color = White,
            )

            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp)
            ) {
                Row(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp))
                        .background(Color(0xFF1E212A)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "№",
                        fontSize = 50.pxToSp(),
                        color = White,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center,
                        modifier = modifier.width(100.dp),
                        style = MaterialTheme.typography.body1,
                    )
                    Text(
                        text = "Ник",
                        fontSize = 50.pxToSp(),
                        color = White,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Start,
                        modifier = modifier.weight(2f),
                        style = MaterialTheme.typography.body1,
                    )
                    Text(
                        text = "Роль",
                        fontSize = 50.pxToSp(),
                        color = White,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center,
                        modifier = modifier.weight(1.2f).padding(end = 16.dp),
                        style = MaterialTheme.typography.body1,
                    )
                }
                gameData.players.forEachIndexed { index, player ->
                    val roleColor = when (player.role) {
                        is GamePlayerRole.Black -> Color(0xFF0D0D13)
                        is GamePlayerRole.Red -> Red
                        else -> WhiteLight
                    }
                    val roleTextColor = when (player.role) {
                        is GamePlayerRole.Black,
                        is GamePlayerRole.Red -> White
                        else -> BlackDark
                    }
                    Row(
                        modifier = Modifier.weight(1f).background(Color(0x66434854)),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = player.number.toString(),
                            fontSize = 50.pxToSp(),
                            color = White,
                            fontWeight = FontWeight.Light,
                            textAlign = TextAlign.Center,
                            modifier = modifier.width(100.dp),
                            style = MaterialTheme.typography.body1,
                        )
                        Text(
                            text = player.name + if (player.isWinner) " \uD83C\uDFC6" else "",
                            fontSize = 50.pxToSp(),
                            color = White,
                            fontWeight = FontWeight.Light,
                            textAlign = TextAlign.Start,
                            modifier = modifier.weight(2f),
                            style = MaterialTheme.typography.body1,
                        )
                        Column(
                            modifier = modifier.weight(1.2f)
                                .height(72.pxToDp())
                                .clip(RoundedCornerShape(8.dp))
                                .background(roleColor),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = player.role.russianText(),
                                fontSize = 50.pxToSp(),
                                color = roleTextColor,
                                fontWeight = FontWeight.Light,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.body1,
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                    }
                    if (index < gameData.players.size - 1) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .height(1.pxToDp())
                                .background(WhiteLight.copy(alpha = 0.5f))
                        ) {}
                    }
                }
            }
        }
    }
}

@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

@Composable
fun Int.pxToSp() = with(LocalDensity.current) { this@pxToSp.toSp() }
