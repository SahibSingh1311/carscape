package com.dmag.carscape.domain.usecase

import com.dmag.carscape.domain.model.Board
import com.dmag.carscape.domain.model.Vehicle
import javax.inject.Inject

data class ExitResult(val exited: Boolean)

class CheckExitUseCase @Inject constructor() {
    operator fun invoke(board: Board, vehicle: Vehicle): ExitResult {
        val headExit = board.exits.find { it.cell == vehicle.head }
        val tailExit = board.exits.find { it.cell == vehicle.occupiedCells().last() }
        val matchingExit = headExit ?: tailExit ?: return ExitResult(exited = false)

        val orientationMatches = matchingExit.orientation == vehicle.orientation
        val colorMatches = matchingExit.colorIndex == null || matchingExit.colorIndex == vehicle.colorIndex

        return ExitResult(exited = orientationMatches && colorMatches)
    }
}