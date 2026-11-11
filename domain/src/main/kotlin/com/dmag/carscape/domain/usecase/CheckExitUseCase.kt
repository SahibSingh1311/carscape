package com.dmag.carscape.domain.usecase

import com.dmag.carscape.domain.model.Board
import com.dmag.carscape.domain.model.Cell
import com.dmag.carscape.domain.model.Orientation
import com.dmag.carscape.domain.model.Vehicle
import javax.inject.Inject

data class ExitResult(val exited: Boolean)

//class CheckExitUseCase @Inject constructor() {
//    operator fun invoke(board: Board, vehicle: Vehicle): ExitResult {
//        val occupied = vehicle.occupiedCells()
//
//        // Check every cell the vehicle currently occupies
//        val matchingExit = occupied.firstNotNullOfOrNull { cell ->
//            board.exits.find { exit ->
//                exit.cell == cell &&
//                        exit.orientation == vehicle.orientation &&
//                        (exit.colorIndex == null || exit.colorIndex == vehicle.colorIndex)
//            }
//        }
//
//        return ExitResult(exited = matchingExit != null)
//    }
//}

class CheckExitUseCase @Inject constructor() {

    operator fun invoke(board: Board, vehicle: Vehicle): ExitResult {
        val candidateExits = board.exits.filter { exit ->
            exit.orientation == vehicle.orientation &&
                    (exit.colorIndex == null || exit.colorIndex == vehicle.colorIndex) &&
                    exit.thickness == vehicle.thickness   // must match thickness
        }
        if (candidateExits.isEmpty()) return ExitResult(false)

        val edgeCells = vehicleEdgeCells(vehicle, board).toSet()
        if (edgeCells.size != vehicle.thickness) return ExitResult(false)

        val exited = candidateExits.any { exit ->
            val exitCells = exit.occupiedCells(board.rows, board.cols).toSet()
            edgeCells == exitCells   // exact match
        }
        return ExitResult(exited)
    }

    private fun vehicleEdgeCells(vehicle: Vehicle, board: Board): List<Cell> {
        val occupied = vehicle.occupiedCells()
        return when (vehicle.orientation) {
            Orientation.HORIZONTAL -> {
                val right = occupied.filter { it.col == board.cols - 1 }
                val left  = occupied.filter { it.col == 0 }
                when {
                    right.size == vehicle.thickness -> right
                    left.size  == vehicle.thickness -> left
                    else -> emptyList()
                }
            }
            Orientation.VERTICAL -> {
                val bottom = occupied.filter { it.row == board.rows - 1 }
                val top    = occupied.filter { it.row == 0 }
                when {
                    bottom.size == vehicle.thickness -> bottom
                    top.size    == vehicle.thickness -> top
                    else -> emptyList()
                }
            }
        }
    }
}