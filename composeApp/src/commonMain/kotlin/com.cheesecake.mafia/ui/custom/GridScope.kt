package com.cheesecake.mafia.ui.custom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import kotlin.math.max

interface GridScope {
    @Stable
    fun Modifier.span(columns: Int = 1, rows: Int = 1) = this.then(
        GridData(columns, rows)
    )

    companion object : GridScope
}

private class GridData(
    val columnSpan: Int,
    val rowSpan: Int,
) : ParentDataModifier {

    override fun Density.modifyParentData(parentData: Any?): Any = this@GridData

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        other as GridData

        if (columnSpan != other.columnSpan) return false
        if (rowSpan != other.rowSpan) return false

        return true
    }

    override fun hashCode(): Int {
        var result = columnSpan
        result = 31 * result + rowSpan
        return result
    }
}

private val Measurable.gridData: GridData?
    get() = parentData as? GridData

private val Measurable.columnSpan: Int
    get() = gridData?.columnSpan ?: 1

private val Measurable.rowSpan: Int
    get() = gridData?.rowSpan ?: 1

data class GridInfo(
    val numChildren: Int,
    val columnSpan: Int,
    val rowSpan: Int,
)


@Composable
fun Grid(
    columns: Int,
    modifier: Modifier = Modifier,
    content: @Composable GridScope.() -> Unit,
) {
    check(columns > 0) { "Columns must be greater than 0" }
    Layout(
        content = { GridScope.content() },
        modifier = modifier,
    ) { measurables, constraints ->
        // calculate how many rows we need
        val standardGrid = GridData(1, 1)
        val spans = measurables.map { measurable -> measurable.gridData ?: standardGrid }
        val gridInfo = calculateGridInfo(spans, columns)
        val rows = gridInfo.sumOf { it.rowSpan }

        // build constraints
        val baseConstraints = Constraints.fixed(
            width = constraints.maxWidth / columns,
            height = constraints.maxHeight / rows,
        )
        val cellConstraints = measurables.map { measurable ->
            val columnSpan = measurable.columnSpan
            val rowSpan = measurable.rowSpan
            Constraints.fixed(
                width = baseConstraints.maxWidth * columnSpan,
                height = baseConstraints.maxHeight * rowSpan
            )
        }

        // measure children
        val placeables = measurables.mapIndexed { index, measurable ->
            measurable.measure(cellConstraints[index])
        }

        // place children
        layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight,
        ) {
            var x = 0
            var y = 0
            var childIndex = 0
            gridInfo.forEach { info ->
                repeat(info.numChildren) { index ->
                    val placeable = placeables[childIndex++]
                    placeable.placeRelative(
                        x = x,
                        y = y,
                    )
                    x += placeable.width
                }
                x = 0
                y += info.rowSpan * baseConstraints.maxHeight
            }
        }
    }
}

private fun calculateGridInfo(
    spans: List<GridData>,
    columns: Int,
): List<GridInfo> {
    var currentColumnSpan = 0
    var currentRowSpan = 0
    var numChildren = 0
    return buildList {
        spans.forEach { span ->
            val columnSpan = span.columnSpan.coerceAtMost(columns)
            val rowSpan = span.rowSpan
            if (currentColumnSpan + columnSpan <= columns) {
                currentColumnSpan += columnSpan
                currentRowSpan = max(currentRowSpan, rowSpan)
                ++numChildren
            } else {
                add(
                    GridInfo(
                        numChildren = numChildren,
                        columnSpan = currentColumnSpan,
                        rowSpan = currentRowSpan
                    )
                )
                currentColumnSpan = columnSpan
                currentRowSpan = rowSpan
                numChildren = 1
            }
        }
        add(
            GridInfo(
                numChildren = numChildren,
                columnSpan = currentColumnSpan,
                rowSpan = currentRowSpan,
            )
        )
    }
}