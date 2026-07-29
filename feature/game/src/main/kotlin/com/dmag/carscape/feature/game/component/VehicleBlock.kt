package com.dmag.carscape.feature.game.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dmag.carscape.core.designsystem.theme.VehicleColors
import com.dmag.carscape.domain.model.Orientation
import com.dmag.carscape.domain.model.Vehicle
import com.dmag.carscape.domain.usecase.SlideDirection
import kotlin.math.roundToInt

@Composable
fun VehicleBlock(
    vehicle: Vehicle,
    cellSizeDp: Dp,
    cellSizePx: Float,
    maxForwardCells: Int,   // legal cells in the RIGHT (horizontal) or DOWN (vertical) direction
    maxBackwardCells: Int,  // legal cells in the LEFT (horizontal) or UP (vertical) direction
    onDragCommitted: (cellsMoved: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHorizontal = vehicle.orientation == Orientation.HORIZONTAL
    val widthCells = if (isHorizontal) vehicle.length else 1
    val heightCells = if (isHorizontal) 1 else vehicle.length

    val baseOffsetX = cellSizeDp * vehicle.head.col
    val baseOffsetY = cellSizeDp * vehicle.head.row
    val width = cellSizeDp * widthCells
    val height = cellSizeDp * heightCells
    val padding = cellSizeDp * 0.08f

    // Live drag offset in pixels, along the vehicle's locked axis only
    var dragOffsetPx by remember(vehicle.head) { mutableStateOf(0f) }

    val maxForwardPx = maxForwardCells * cellSizePx
    val maxBackwardPx = -maxBackwardCells * cellSizePx

    val extraOffsetDp = with(androidx.compose.ui.platform.LocalDensity.current) { dragOffsetPx.toDp() }

    Box(
        modifier = modifier
            .offset(
                x = baseOffsetX + padding + (if (isHorizontal) extraOffsetDp else 0.dp),
                y = baseOffsetY + padding + (if (!isHorizontal) extraOffsetDp else 0.dp)
            )
            .size(width - padding * 2, height - padding * 2)
            .clip(RoundedCornerShape(cellSizeDp * 0.2f))
            .background(VehicleColors[vehicle.colorIndex % VehicleColors.size])
            .pointerInput(vehicle.id, maxForwardCells, maxBackwardCells) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val delta = if (isHorizontal) dragAmount.x else dragAmount.y
                        val newOffset = (dragOffsetPx + delta).coerceIn(maxBackwardPx, maxForwardPx)
                        dragOffsetPx = newOffset
                    },
                    onDragEnd = {
                        val cellsMoved = (dragOffsetPx / cellSizePx).roundToInt()
                        dragOffsetPx = 0f
                        if (cellsMoved != 0) {
                            onDragCommitted(cellsMoved)
                        }
                    }
                )
            }
    )
}