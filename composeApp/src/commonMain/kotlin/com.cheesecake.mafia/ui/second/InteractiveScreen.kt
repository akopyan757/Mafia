package com.cheesecake.mafia.ui.second

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.LogoTint
import com.cheesecake.mafia.common.PlayersColors
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.imageResources
import com.cheesecake.mafia.data.InteractiveScreenState
import com.cheesecake.mafia.viewModel.InteractiveScreenViewModel
import org.koin.compose.koinInject

@Composable
fun InteractiveScreen(
    viewModel: InteractiveScreenViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()
    val settings by viewModel.settings.collectAsState()
    if (!settings.showInteractive) {
        InteractiveNewGameScreen()
    } else if (state is InteractiveScreenState.LiveGame) {
        InteractiveLiveScreen((state as InteractiveScreenState.LiveGame).state, settings)
    } else {
        val gameData = (state as? InteractiveScreenState.FinishGame)?.state
        if (gameData != null) {
            InteractiveFinishedScreen(gameData)
        } else {
            InteractiveNewGameScreen()
        }
    }
}

@Composable
fun InteractiveNewGameScreen() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        Box {
            Image(
                painter = imageResources("background.xml"),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
            Image(
                painter = imageResources("logo_gt.xml"),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                colorFilter = ColorFilter.tint(LogoTint),
                modifier = Modifier.align(Alignment.Center).fillMaxHeight(0.6f)
            )
        }
    }
}

@Composable
fun PlayerNumberWidget(
    modifier: Modifier = Modifier,
    number: Int,
    isAlive: Boolean = true,
) {
    val index = (number - 1) % PlayersColors.size
    val backgroundColor = if (isAlive) PlayersColors[index] else BlackDark
    val tintColor = if (isAlive) White else White.copy(alpha = 0.4f)
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        backgroundColor = backgroundColor,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.h3,
                color = tintColor,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}