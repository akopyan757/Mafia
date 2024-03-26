package com.cheesecake.mafia.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
actual fun fontFamily(): FontFamily = FontFamily(
    fontResources("ubuntu.xml", FontWeight.Light, FontStyle.Normal),
    fontResources("ubuntu_regular.xml", FontWeight.Normal, FontStyle.Normal),
    fontResources("ubuntu_medium.xml", FontWeight.Medium, FontStyle.Normal),
    fontResources("ubuntu_bold.xml", FontWeight.Bold, FontStyle.Normal),
)