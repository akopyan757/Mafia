package com.cheesecake.mafia.common

import androidx.compose.ui.input.key.Key

enum class RussianKey(val keyCode: Long) {
    M(72062319038824448),
    N(72062276089151488),
    B(72062233139478528),
    C(72062271794184192),
    D(72062207369674752),
    S(72062314743857152),
    W(72062293269020672);

    val key: Key get() = Key(keyCode)
}