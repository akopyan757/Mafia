package com.cheesecake.mafia.ui.finishedGame

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cheesecake.mafia.data.DayType
import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.GameFinishResult
import com.cheesecake.mafia.data.GamePlayerData
import com.cheesecake.mafia.data.GamePlayerRole

@Preview
@Composable
fun CaptureFinishedScreenPreview() {
    CaptureFinishedScreen(
        Modifier,
        GameData(
            title = "",
            date = "04.05.2024",
            lastRound = 0,
            lastDayType = DayType.Day,
            finishResult = GameFinishResult.BlackWin,
            totalTime = 0L,
            players = listOf(
                GamePlayerData(number = 1, name = "Кардинал", isWinner = true, role = GamePlayerRole.Black.Mafia),
                GamePlayerData(number = 2, name = "Скади", isWinner = false, role = GamePlayerRole.Red.Civilian),
                GamePlayerData(number = 3, name = "Визирь", isWinner = true, role = GamePlayerRole.Black.Don),
                GamePlayerData(number = 4, name = "Чикибамбони", isWinner = false, role = GamePlayerRole.Red.Sheriff),
                GamePlayerData(number = 5, name = "Синий", isWinner = false, role = GamePlayerRole.Red.Civilian),
                GamePlayerData(number = 5, name = "Винчестер", isWinner = false, role = GamePlayerRole.Red.Civilian),
                GamePlayerData(number = 7, name = "Сова", isWinner = true, role = GamePlayerRole.Black.Mafia),
                GamePlayerData(number = 8, name = "Хенгмен", isWinner = false, role = GamePlayerRole.Red.Doctor),
                GamePlayerData(number = 9, name = "Кроули", isWinner = false, role = GamePlayerRole.White.Maniac),
                GamePlayerData(number = 10, name = "Пианист", isWinner = false, role = GamePlayerRole.Red.Civilian),
            )
        )
    )
}