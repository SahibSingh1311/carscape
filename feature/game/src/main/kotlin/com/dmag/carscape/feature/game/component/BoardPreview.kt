package com.dmag.carscape.feature.game.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmag.carscape.core.designsystem.theme.CarScapeTheme
import com.dmag.carscape.domain.model.Board
import com.dmag.carscape.domain.model.Cell
import com.dmag.carscape.domain.model.ExitGate
import com.dmag.carscape.domain.model.Orientation
import com.dmag.carscape.domain.model.Vehicle

private val sampleBoard = Board(
    rows = 6,
    cols = 6,
    vehicles = listOf(
        Vehicle("red_bus", Orientation.HORIZONTAL, 2, Cell(2, 1), colorIndex = 0),
        Vehicle("blue_car", Orientation.VERTICAL, 2, Cell(0, 4), colorIndex = 1),
        Vehicle("green_car", Orientation.VERTICAL, 3, Cell(3, 0), colorIndex = 2)
    ),
    exits = listOf(
        ExitGate(Cell(2, 5), Orientation.HORIZONTAL, colorIndex = 0)
    )
)

@Preview(showBackground = true)
@Composable
fun BoardCanvasPreview() {
    CarScapeTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            BoardCanvas(board = sampleBoard) { cellSizePx ->
                val cellSizeDp = with(androidx.compose.ui.platform.LocalDensity.current) { cellSizePx.toDp() }
                sampleBoard.vehicles.forEach { vehicle ->
                    VehicleBlock(
                        vehicle = vehicle,
                        cellSizeDp = cellSizeDp,
                        cellSizePx = cellSizePx,
                        maxForwardCells = 2,   // placeholder — real value comes from GetValidSlideDistanceUseCase
                        maxBackwardCells = 2,  // placeholder — same
                        onDragCommitted = { /* no-op in preview, no ViewModel here */ }
                    )
                }
            }
        }
    }
}