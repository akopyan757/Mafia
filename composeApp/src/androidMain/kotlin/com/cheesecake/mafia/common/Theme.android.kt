package com.cheesecake.mafia.common

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.cheesecake.mafia.R

actual val fontFamily: FontFamily = FontFamily(
    Font(R.font.ubuntu, FontWeight.Light, FontStyle.Normal),
    Font(R.font.ubuntu_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.ubuntu_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.ubuntu_bold, FontWeight.Bold, FontStyle.Normal),
)