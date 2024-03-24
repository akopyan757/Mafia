package com.cheesecake.mafia.state

data class SquareData(
    val topIndexes: List<Int>,
    val rightIndexes: List<Int>,
    val leftIndexes: List<Int>,
    val bottomIndexes: List<Int>,
)