package com.cheesecake.mafia.common

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val lightColors = lightColors(
    primary = Black,
    secondary = BlackDark,
    onPrimary = White,
    onSecondary = White,
)

expect val fontFamily: FontFamily

fun getFontFamily() = fontFamily

val typography = Typography(
    h3 = TextStyle(
        fontFamily = getFontFamily(),
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal,
        fontSize = 24.sp,
        lineHeight = 26.sp,
        color = Black,
    ),
    h5 = TextStyle(
        fontFamily = getFontFamily(),
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal,
        fontSize = 16.sp,
        lineHeight = 18.sp,
        color = Black,
    ),
    h6 = TextStyle(
        fontFamily = getFontFamily(),
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        color = Black,
    ),
    body1 = TextStyle(
        fontFamily = getFontFamily(),
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal,
        fontSize = 16.sp,
        lineHeight = 18.sp,
        color = BlackDark,
    ),
    subtitle1 = TextStyle(
        fontFamily = getFontFamily(),
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        color = GrayLight,
    ),
    caption = TextStyle(
        fontFamily = getFontFamily(),
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal,
        fontSize = 10.sp,
        lineHeight = 16.sp,
        color = White,
    ),
)

@Composable
fun ProjectTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = lightColors,
        typography = typography,
        content = content,
    )
}