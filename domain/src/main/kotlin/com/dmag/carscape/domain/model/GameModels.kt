package com.dmag.carscape.domain.model

enum class Orientation { HORIZONTAL, VERTICAL }

enum class LevelDifficulty { NORMAL, HARD, VERY_HARD }

data class Cell(val row: Int, val col: Int)

data class Vehicle(
    val id: String,
    val orientation: Orientation,
    val length: Int,           // 2 or 3 cells
    val thickness: Int = 1, // cross axis (1 for cars, 2 for bus/truck, etc.)
    val head: Cell,            // top-left-most occupied cell
    val colorIndex: Int
) {
    fun occupiedCells(): List<Cell> {
        val cells = mutableListOf<Cell>()
        for (along in 0 until length) {
            for (across in 0 until thickness) {
                cells += if (orientation == Orientation.HORIZONTAL) {
                    Cell(row = head.row + across, col = head.col + along)
                } else {
                    Cell(row = head.row + along, col = head.col + across)
                }
            }
        }
        return cells
    }
}

data class ExitGate(
    val cell: Cell,                // the border cell the gate sits on
    val orientation: Orientation,  // which vehicle orientation can use it
    val thickness: Int = 1,
    val colorIndex: Int?           // null = accepts any color; non-null = only matching vehicle
) {
    /** All cells this exit occupies on the border */
    fun occupiedCells(boardRows: Int, boardCols: Int): List<Cell> {
        return (0 until thickness).map { offset ->
            if (orientation == Orientation.HORIZONTAL) {
                // Exit sits on left or right border; thickness grows along rows
                Cell(row = cell.row + offset, col = cell.col)
            } else {
                // Exit sits on top or bottom border; thickness grows along cols
                Cell(row = cell.row, col = cell.col + offset)
            }
        }
    }
}

data class Board(
    val rows: Int,
    val cols: Int,
    val vehicles: List<Vehicle> = emptyList(),
    val exits: List<ExitGate> = emptyList(),
    val timeLimitSeconds: Int = 60,
    val coinReward: Int = 10,
    val difficulty: LevelDifficulty = LevelDifficulty.NORMAL,
    val diamondReward: Int = 0,
    val optimalMoves: Int = 0
)

data class GameState(
    val board: Board,
    val moves: Int = 0,
    val isSolved: Boolean = false
)