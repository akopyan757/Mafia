package com.cheesecake.mafia.ui.second

import com.cheesecake.mafia.state.SquareData

fun createSquare(count: Int): SquareData {
    val gridColumns = when {
        count >= 19 -> 6
        count in 13..18 -> 5
        count in 9..12 -> 4
        else -> 3
    }
    val gridMiddleRows = maxOf(((count + count % 2) - 2 * gridColumns) / 2, 1)
    val cellsCount = 2 * (gridMiddleRows + gridColumns)
    val offset = if (gridColumns % 2 == 0) gridColumns / 2 else gridColumns / 2 + count % 2
    val filterIndex: ((Int) -> Int) = { if (it in (0 until count)) it else -1 }
    val top = ((cellsCount - offset until cellsCount) +
            (0 until gridColumns - offset).toList()).map(filterIndex)
    val right = (gridColumns - offset until gridColumns + gridMiddleRows - offset).toList().map(filterIndex)
    val bottom  = (gridColumns + gridMiddleRows - offset until 2 * gridColumns + gridMiddleRows - offset).toList().reversed().map(filterIndex)
    val left = (2 * gridColumns + gridMiddleRows - offset until 2 * (gridColumns + gridMiddleRows) - offset).toList().reversed().map(filterIndex)
    return SquareData(top, right, left, bottom)
}