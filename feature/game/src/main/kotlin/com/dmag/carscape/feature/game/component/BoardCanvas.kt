package com.dmag.carscape.feature.game.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dmag.carscape.core.designsystem.theme.RoadGray
import com.dmag.carscape.core.designsystem.theme.RoadGrayLight
import com.dmag.carscape.domain.model.Board

@Composable
fun BoardCanvas(
    board: Board,
    modifier: Modifier = Modifier,
    content: @Composable (cellSizePx: Float) -> Unit
) {
    var boardWidthPx by remember { mutableStateOf(0f) }
    val cellSizePx = if (board.cols > 0) boardWidthPx / board.cols else 0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(board.cols.toFloat() / board.rows.toFloat())
            .padding(8.dp)
            .onSizeChanged { boardWidthPx = it.width.toFloat() }
    ) {
        Canvas(modifier = Modifier.fillMaxWidth()) {
            val cell = size.width / board.cols
            for (row in 0 until board.rows) {
                for (col in 0 until board.cols) {
                    drawRect(
                        color = if ((row + col) % 2 == 0) RoadGray else RoadGrayLight,
                        topLeft = Offset(col * cell, row * cell),
                        size = Size(cell, cell)
                    )
                }
            }
            // Outer border
            drawRect(
                color = RoadGrayLight,
                topLeft = Offset.Zero,
                size = Size(size.width, size.height),
                style = Stroke(width = 4f)
            )
        }

        if (cellSizePx > 0) {
            content(cellSizePx)
        }
    }
}