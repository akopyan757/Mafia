package com.cheesecake.mafia.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
actual fun fontFamily(): FontFamily = FontFamily(
    fontResources("ubuntu_light.ttf", FontWeight.Light, FontStyle.Normal),
    fontResources("ubuntu_regular.ttf", FontWeight.Normal, FontStyle.Normal),
    fontResources("ubuntu_medium.ttf", FontWeight.Medium, FontStyle.Normal),
    fontResources("ubuntu_bold.ttf", FontWeight.Bold, FontStyle.Normal),
)