package com.cheesecake.mafia.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun imageResources(imageRes: String): Painter {
    return painterResource(resource = DrawableResource("drawable/$imageRes"))
}