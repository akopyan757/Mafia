package com.cheesecake.mafia.common

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

actual val fontFamily: FontFamily = FontFamily(
    Font(resource = "font/ubuntu_light.ttf", FontWeight.Light, FontStyle.Normal),
    Font(resource = "font/ubuntu_regular.ttf", FontWeight.Normal, FontStyle.Normal),
    Font(resource = "font/ubuntu_medium.ttf", FontWeight.Medium, FontStyle.Normal),
    Font(resource = "font/ubuntu_bold.ttf", FontWeight.Bold, FontStyle.Normal),
)