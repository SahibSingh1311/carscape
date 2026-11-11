package com.dmag.carscape.domain.usecase.mocks

import com.dmag.carscape.domain.model.Board
import com.dmag.carscape.domain.model.Cell
import com.dmag.carscape.domain.model.ExitGate
import com.dmag.carscape.domain.model.LevelDifficulty
import com.dmag.carscape.domain.model.Orientation
import com.dmag.carscape.domain.model.Vehicle

object VehicleMocks {

    /**
     * thickness defaults from length:
     *  - length <= 3 → 1
     *  - length >= 4 → 2
     * Override thickness when you need a specific case.
     */
    fun vehicle(
        id: String = "v1",
        orientation: Orientation = Orientation.HORIZONTAL,
        length: Int = 2,
        head: Cell = Cell(0, 0),
        colorIndex: Int = 0,
        thickness: Int = if (length >= 4) 2 else 1
    ): Vehicle = Vehicle(
        id = id,
        orientation = orientation,
        length = length,
        thickness = thickness,
        head = head,
        colorIndex = colorIndex
    )

    // Convenience helpers
    fun smallCar(
        id: String = "small",
        orientation: Orientation = Orientation.HORIZONTAL,
        head: Cell = Cell(0, 0),
        colorIndex: Int = 0
    ) = vehicle(id, orientation, length = 2, head = head, colorIndex = colorIndex)

    fun sedan(
        id: String = "sedan",
        orientation: Orientation = Orientation.HORIZONTAL,
        head: Cell = Cell(0, 0),
        colorIndex: Int = 0
    ) = vehicle(id, orientation, length = 3, head = head, colorIndex = colorIndex)

    fun suv(
        id: String = "suv",
        orientation: Orientation = Orientation.HORIZONTAL,
        head: Cell = Cell(0, 0),
        colorIndex: Int = 0
    ) = vehicle(id, orientation, length = 4, head = head, colorIndex = colorIndex)

    fun limo(
        id: String = "limo",
        orientation: Orientation = Orientation.HORIZONTAL,
        head: Cell = Cell(0, 0),
        colorIndex: Int = 0
    ) = vehicle(id, orientation, length = 5, head = head, colorIndex = colorIndex)

    fun bus(
        id: String = "bus",
        orientation: Orientation = Orientation.HORIZONTAL,
        head: Cell = Cell(0, 0),
        colorIndex: Int = 0
    ) = vehicle(id, orientation, length = 6, head = head, colorIndex = colorIndex)
}

object BoardMocks {

    fun board(
        rows: Int = 6,
        cols: Int = 6,
        vehicles: List<Vehicle> = emptyList(),
        exits: List<ExitGate> = emptyList(),
        timeLimitSeconds: Int = 60,
        coinReward: Int = 10,
        difficulty: LevelDifficulty = LevelDifficulty.NORMAL,
        diamondReward: Int = 0,
        optimalMoves: Int = 0
    ): Board = Board(
        rows = rows,
        cols = cols,
        vehicles = vehicles,
        exits = exits,
        timeLimitSeconds = timeLimitSeconds,
        coinReward = coinReward,
        difficulty = difficulty,
        diamondReward = diamondReward,
        optimalMoves = optimalMoves
    )

    /** Empty board – useful for edge-only slide tests */
    fun empty(rows: Int = 6, cols: Int = 6): Board =
        board(rows = rows, cols = cols)

    /** Board that only contains the given vehicles */
    fun withVehicles(
        vararg vehicles: Vehicle,
        rows: Int = 6,
        cols: Int = 6
    ): Board = board(rows = rows, cols = cols, vehicles = vehicles.toList())

    /** Board with vehicles + exits */
    fun withVehiclesAndExits(
        vehicles: List<Vehicle>,
        exits: List<ExitGate>,
        rows: Int = 6,
        cols: Int = 6
    ): Board = board(rows = rows, cols = cols, vehicles = vehicles, exits = exits)

    // ----- Exit helpers -----

    fun exit(
        row: Int,
        col: Int,
        orientation: Orientation,
        thickness: Int = 1,
        colorIndex: Int? = null
    ): ExitGate = ExitGate(
        cell = Cell(row, col),
        orientation = orientation,
        thickness = thickness,
        colorIndex = colorIndex
    )

    fun rightExit(
        row: Int,
        cols: Int = 6,
        thickness: Int = 1,
        colorIndex: Int? = 0
    ): ExitGate = exit(
        row = row,
        col = cols - 1,
        orientation = Orientation.HORIZONTAL,
        thickness = thickness,
        colorIndex = colorIndex
    )

    fun leftExit(
        row: Int,
        thickness: Int = 1,
        colorIndex: Int? = 0
    ): ExitGate = exit(
        row = row,
        col = 0,
        orientation = Orientation.HORIZONTAL,
        thickness = thickness,
        colorIndex = colorIndex
    )

    fun bottomExit(
        col: Int,
        rows: Int = 6,
        thickness: Int = 1,
        colorIndex: Int? = 0
    ): ExitGate = exit(
        row = rows - 1,
        col = col,
        orientation = Orientation.VERTICAL,
        thickness = thickness,
        colorIndex = colorIndex
    )

    fun topExit(
        col: Int,
        thickness: Int = 1,
        colorIndex: Int? = 0
    ): ExitGate = exit(
        row = 0,
        col = col,
        orientation = Orientation.VERTICAL,
        thickness = thickness,
        colorIndex = colorIndex
    )

    // ----- Thickness helpers -----

    fun rightWide(
        row: Int,
        cols: Int = 6,
        colorIndex: Int? = 0
    ): ExitGate = rightExit(row = row, cols = cols, thickness = 2, colorIndex = colorIndex)

    fun leftWide(
        row: Int,
        colorIndex: Int? = 0
    ): ExitGate = leftExit(row = row, thickness = 2, colorIndex = colorIndex)

    fun bottomWide(
        col: Int,
        rows: Int = 6,
        colorIndex: Int? = 0
    ): ExitGate = bottomExit(col = col, rows = rows, thickness = 2, colorIndex = colorIndex)

    fun topWide(
        col: Int,
        colorIndex: Int? = 0
    ): ExitGate = topExit(col = col, thickness = 2, colorIndex = colorIndex)
}