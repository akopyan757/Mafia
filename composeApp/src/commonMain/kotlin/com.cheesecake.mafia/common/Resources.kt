package com.cheesecake.mafia.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
expect fun imageResources(imageRes: String): Painter

@Composable
expect fun fontResources(fontRes: String, weight: FontWeight, style: FontStyle): Font