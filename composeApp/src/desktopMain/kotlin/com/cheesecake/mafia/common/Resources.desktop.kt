package com.cheesecake.mafia.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun imageResources(imageRes: String): Painter {
    return painterResource(resource = DrawableResource("drawable/$imageRes"))
}

@Composable
actual fun fontResources(
    fontRes: String,
    weight: FontWeight,
    style: FontStyle,
): Font = Font(resource = "font/$fontRes", FontWeight.Light, FontStyle.Normal)