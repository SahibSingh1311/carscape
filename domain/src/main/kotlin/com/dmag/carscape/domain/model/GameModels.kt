package com.dmag.carscape.domain.model

enum class Orientation { HORIZONTAL, VERTICAL }

enum class LevelDifficulty { NORMAL, HARD, VERY_HARD }

data class Cell(val row: Int, val col: Int)

data class Vehicle(
    val id: String,
    val orientation: Orientation,
    val length: Int,           // 2 or 3 cells
    val head: Cell,            // top-left-most occupied cell
    val colorIndex: Int
) {
    fun occupiedCells(): List<Cell> = (0 until length).map { offset ->
        if (orientation == Orientation.HORIZONTAL) head.copy(col = head.col + offset)
        else head.copy(row = head.row + offset)
    }
}

data class ExitGate(
    val cell: Cell,                // the border cell the gate sits on
    val orientation: Orientation,  // which vehicle orientation can use it
    val colorIndex: Int?           // null = accepts any color; non-null = only matching vehicle
)

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