package com.dmag.carscape.domain.usecase

import com.dmag.carscape.domain.model.Board
import com.dmag.carscape.domain.model.Orientation
import com.dmag.carscape.domain.model.Vehicle
import javax.inject.Inject

enum class SlideDirection { UP, DOWN, LEFT, RIGHT }

class GetValidSlideDistanceUseCase @Inject constructor() {
    operator fun invoke(board: Board, vehicle: Vehicle, direction: SlideDirection): Int {
        val step = when (direction) {
            SlideDirection.UP, SlideDirection.LEFT -> -1
            SlideDirection.DOWN, SlideDirection.RIGHT -> 1
        }
        val isVerticalMove = direction == SlideDirection.UP || direction == SlideDirection.DOWN
        val vehicleIsVertical = vehicle.orientation == Orientation.VERTICAL

        if (isVerticalMove != vehicleIsVertical) return 0

        val occupiedByOthers = board.vehicles
            .filter { it.id != vehicle.id }
            .flatMap { it.occupiedCells() }
            .toSet()

        var distance = 0
        while (true) {
            val nextDistance = distance + step
            val testCells = vehicle.occupiedCells().map { cell ->
                if (vehicleIsVertical) cell.copy(row = cell.row + nextDistance)
                else cell.copy(col = cell.col + nextDistance)
            }
            val outOfBounds = testCells.any { it.row !in 0 until board.rows || it.col !in 0 until board.cols }
            val blocked = testCells.any { it in occupiedByOthers }
            if (outOfBounds || blocked) break
            distance = nextDistance
        }
        return distance
    }
}