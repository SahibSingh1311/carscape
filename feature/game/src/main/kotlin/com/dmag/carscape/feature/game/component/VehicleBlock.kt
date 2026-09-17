package com.dmag.carscape.feature.game.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dmag.carscape.core.designsystem.R
import com.dmag.carscape.core.designsystem.theme.VehicleColors
import com.dmag.carscape.domain.model.ExitGate
import com.dmag.carscape.domain.model.Orientation
import com.dmag.carscape.domain.model.Vehicle
import kotlin.math.roundToInt

@Composable
fun VehicleBlock(
    vehicle: Vehicle,
    exits: List<ExitGate>,
    cellSizeDp: Dp,
    cellSizePx: Float,
    maxForwardCells: Int,   // legal cells in the RIGHT (horizontal) or DOWN (vertical) direction
    maxBackwardCells: Int,  // legal cells in the LEFT (horizontal) or UP (vertical) direction
    onDragCommitted: (cellsMoved: Int) -> Unit,
    onTap: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isHorizontal = vehicle.orientation == Orientation.HORIZONTAL
    val widthCells = if (isHorizontal) vehicle.length else vehicle.thickness
    val heightCells = if (isHorizontal) vehicle.thickness else vehicle.length

    val baseOffsetX = cellSizeDp * vehicle.head.col
    val baseOffsetY = cellSizeDp * vehicle.head.row
    val width = cellSizeDp * widthCells
    val height = cellSizeDp * heightCells
    val padding = cellSizeDp * 0.04f

    // Live drag offset in pixels, along the vehicle's locked axis only
    var dragOffsetPx by remember(vehicle.head) { mutableStateOf(0f) }

    val maxForwardPx = maxForwardCells * cellSizePx
    val maxBackwardPx = -maxBackwardCells * cellSizePx

    val extraOffsetDp = with(LocalDensity.current) { dragOffsetPx.toDp() }
    // A vehicle's matching exit is fixed on a board edge — by construction it always
    // sits at column/row 0 or the far edge, never anywhere else, so checking "== 0"
    // is enough to know which side without needing the board's full dimensions here.
    val matchingExit = exits.firstOrNull {
        it.orientation == vehicle.orientation && it.colorIndex == vehicle.colorIndex
    }

    Box(
        modifier = modifier
            .offset(
                x = baseOffsetX + padding + (if (isHorizontal) extraOffsetDp else 0.dp),
                y = baseOffsetY + padding + (if (!isHorizontal) extraOffsetDp else 0.dp)
            )
            .size(width - padding * 2, height - padding * 2)
            .pointerInput(vehicle.id) {
                detectTapGestures(onTap = { onTap() })
            }
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
    ) {
        val baseColor = VehicleColors[vehicle.colorIndex % VehicleColors.size]
        if (isHorizontal) {
            // Art is authored facing RIGHT by convention. Mirror it horizontally
            // (scaleX = -1) when the matching exit is on the LEFT edge, so the
            // vehicle always visually faces the direction it needs to exit toward.
            val facesLeft = matchingExit?.cell?.col == 0
            VehicleArt(
                vehicle = vehicle,
                color = baseColor,
                isVertical = false,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(scaleX = if (facesLeft) -1f else 1f)
            )
        } else {
            // Rotating rightward-facing art +90° makes it face DOWN;
            // -90° makes it face UP. Pick based on which edge the exit is on.
            val facesUp = matchingExit?.cell?.row == 0
            VehicleArt(
                vehicle = vehicle,
                color = baseColor,
                isVertical = true,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(rotationZ = if (facesUp) -180f else 0f)
            )
        }
    }
}

@Composable
private fun VehicleArt(vehicle: Vehicle, color: Color, isVertical: Boolean, modifier: Modifier = Modifier) {
    val res = drawableForLength(vehicle.length, isVertical)
    if (res != null) {
        Image(
            painter = painterResource(id = res),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color, BlendMode.Modulate),
            contentScale = ContentScale.FillBounds,
            modifier = modifier
        )
    } else {
        Canvas(modifier = modifier) {
            drawVehicleBody(vehicle.length, color, size.width, size.height)
        }
    }
}

private fun drawableForLength(length: Int, isVertical: Boolean): Int? = when (length) {
    2 -> if(!isVertical) R.drawable.small_car_1_h else R.drawable.small_car_1_v
    3 -> if(!isVertical) R.drawable.sedan_car_1_h else R.drawable.sedan_car_1_v
    4 -> if(!isVertical) R.drawable.pickup_truck_1_h else R.drawable.pickup_truck_1_v
    5 -> if(!isVertical) R.drawable.limo_car_1_h else R.drawable.limo_car_1_v
    else -> if(!isVertical) R.drawable.bus_1_h else R.drawable.bus_1_v
}

// Vector-drawn fallback body — used only if drawableForLength ever returns null
// (kept as a safety net; currently every length maps to real art above).
private fun DrawScope.drawVehicleBody(length: Int, color: Color, w: Float, h: Float) {
    val cornerRadius = h * 0.35f
    val gradient = Brush.verticalGradient(
        colors = listOf(lerp(color, Color.White, 0.25f), color, lerp(color, Color.Black, 0.15f)),
        startY = 0f,
        endY = h
    )
    drawRoundRect(
        brush = gradient,
        size = Size(w, h),
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )

    val windowColor = Color(0xFFDCEEFF).copy(alpha = 0.88f)
    val windowInset = h * 0.18f
    val windowHeight = h - windowInset * 2

    when {
        length <= 2 -> {
            val windowWidth = w
            drawRoundRect(
                color = windowColor,
                topLeft = Offset((w - windowWidth) / 2, windowInset),
                size = Size(windowWidth, windowHeight),
                cornerRadius = CornerRadius(windowHeight * 0.3f, windowHeight * 0.3f)
            )
        }
        length == 3 -> {
            val segment = w * 0.28f
            val gap = w * 0.08f
            drawRoundRect(color = windowColor, topLeft = Offset(w * 0.15f, windowInset), size = Size(segment, windowHeight), cornerRadius = CornerRadius(windowHeight * 0.25f))
            drawRoundRect(color = windowColor, topLeft = Offset(w * 0.15f + segment + gap, windowInset), size = Size(segment, windowHeight), cornerRadius = CornerRadius(windowHeight * 0.25f))
        }
        length == 4 -> {
            val windowWidth = w * 0.62f
            drawRoundRect(
                color = windowColor,
                topLeft = Offset((w - windowWidth) / 2, windowInset * 0.7f),
                size = Size(windowWidth, h - windowInset * 1.4f),
                cornerRadius = CornerRadius(windowHeight * 0.2f)
            )
        }
        length == 5 -> {
            val count = 3
            val segment = w * 0.16f
            val totalGaps = w * 0.7f - segment * count
            val gap = totalGaps / (count - 1)
            var x = w * 0.15f
            repeat(count) {
                drawRoundRect(color = windowColor, topLeft = Offset(x, windowInset), size = Size(segment, windowHeight), cornerRadius = CornerRadius(windowHeight * 0.2f))
                x += segment + gap
            }
        }
        else -> {
            val count = (length - 1).coerceAtMost(6)
            val margin = w * 0.08f
            val segment = w * 0.1f
            val totalGaps = (w - margin * 2) - segment * count
            val gap = if (count > 1) totalGaps / (count - 1) else 0f
            var x = margin
            repeat(count) {
                drawRoundRect(color = windowColor, topLeft = Offset(x, windowInset), size = Size(segment, windowHeight), cornerRadius = CornerRadius(windowHeight * 0.15f))
                x += segment + gap
            }
            drawRect(color = Color.White.copy(alpha = 0.22f), topLeft = Offset(0f, h * 0.78f), size = Size(w, h * 0.06f))
        }
    }

    val wheelWidth = w * 0.14f
    val wheelHeight = h * 0.16f
    val wheelY = h - wheelHeight * 0.5f
    val wheelColor = Color(0xFF1A1512)
    drawRoundRect(color = wheelColor, topLeft = Offset(w * 0.12f, wheelY), size = Size(wheelWidth, wheelHeight), cornerRadius = CornerRadius(wheelHeight / 2))
    drawRoundRect(color = wheelColor, topLeft = Offset(w * 0.88f - wheelWidth, wheelY), size = Size(wheelWidth, wheelHeight), cornerRadius = CornerRadius(wheelHeight / 2))
}