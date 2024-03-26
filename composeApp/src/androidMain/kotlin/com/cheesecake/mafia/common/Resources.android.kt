package com.cheesecake.mafia.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
actual fun imageResources(imageRes: String): Painter {
    val context = LocalContext.current
    val name = imageRes.substringBefore(".")
    return painterResource(id = context.resIdByName(name, "drawable"))
}

@Composable
actual fun fontResources(fontRes: String, weight: FontWeight, style: FontStyle): Font {
    val context = LocalContext.current
    val id = context.resIdByName(fontRes, "font")
    return Font(id, weight, style)
}

@SuppressLint("DiscouragedApi")
fun Context.resIdByName(resIdName: String?, resType: String): Int {
    resIdName?.let {
        return resources.getIdentifier(it, resType, packageName)
    }
    throw Resources.NotFoundException()
}
