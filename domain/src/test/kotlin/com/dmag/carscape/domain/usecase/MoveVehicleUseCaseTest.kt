package com.dmag.carscape.domain.usecase

import com.dmag.carscape.domain.model.Cell
import com.dmag.carscape.domain.model.GameState
import com.dmag.carscape.domain.model.Orientation
import com.dmag.carscape.domain.usecase.mocks.BoardMocks
import com.dmag.carscape.domain.usecase.mocks.VehicleMocks
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MoveVehicleUseCaseTest {

    private val useCase = MoveVehicleUseCase(CheckExitUseCase())

    @Test
    fun `moving a vehicle increments the move counter`() {
        val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL)
        val board = BoardMocks.withVehicles(vehicle)
        val state = GameState(board = board)

        val result = useCase(state, "a", distance = 2)

        assertEquals(1, result.moves)
    }

    @Test
    fun `zero distance is a no-op and returns the same state`() {
        val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL)
        val board = BoardMocks.withVehicles(vehicle)
        val state = GameState(board = board)

        val result = useCase(state, "a", distance = 0)

        assertEquals(state, result)
    }

    @Test
    fun `moving an unknown vehicle id returns unchanged state`() {
        val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL)
        val board = BoardMocks.withVehicles(vehicle)
        val state = GameState(board = board)

        val result = useCase(state, "nonexistent", distance = 2)

        assertEquals(state, result)
    }

    @Test
    fun `horizontal vehicle head and tail update correctly after a move`() {
        val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL, Cell(0, 0))
        val board = BoardMocks.withVehicles(vehicle)
        val state = GameState(board = board)

        val result = useCase(state, "a", distance = 3)
        val moved = result.board.vehicles.first { it.id == "a" }

        assertEquals(Cell(0, 3), moved.head)
    }

    @Test
    fun `vertical vehicle head updates correctly after a move`() {
        val vehicle = VehicleMocks.sedan("a", Orientation.VERTICAL, Cell(0, 2))
        val board = BoardMocks.withVehicles(vehicle)
        val state = GameState(board = board)

        val result = useCase(state, "a", distance = 3)
        val moved = result.board.vehicles.first { it.id == "a" }

        assertEquals(Cell(3, 2), moved.head)
    }

    @Test
    fun `moving the last vehicle onto its exit solves the level and removes it`() {
        val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL, Cell(2, 2))
        val exit = BoardMocks.rightExit(row = 2, cols = 6, colorIndex = 0)
        val board = BoardMocks.withVehiclesAndExits(
            vehicles = listOf(vehicle),
            exits = listOf(exit)
        )
        val state = GameState(board = board)

        val result = useCase(state, "a", distance = 3) // tail lands on col5

        assertTrue(result.isSolved)
        assertTrue(result.board.vehicles.isEmpty())
    }

    @Test
    fun `exiting one of several vehicles does not solve the level`() {
        val exiting = VehicleMocks.smallCar("a", Orientation.HORIZONTAL, Cell(2, 2))
        val remaining = VehicleMocks.sedan("b", Orientation.VERTICAL, Cell(0, 0))
        val exit = BoardMocks.rightExit(row = 2, cols = 6, colorIndex = 0)
        val board = BoardMocks.withVehiclesAndExits(
            vehicles = listOf(exiting, remaining),
            exits = listOf(exit)
        )
        val state = GameState(board = board)

        val result = useCase(state, "a", distance = 3)

        assertEquals(false, result.isSolved)
        assertEquals(1, result.board.vehicles.size)
        assertEquals("b", result.board.vehicles.first().id)
    }

    @Test
    fun `move that does not reach an exit keeps vehicle on the board`() {
        val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL, Cell(2, 0))
        val exit = BoardMocks.rightExit(row = 2, cols = 6, colorIndex = 0)
        val board = BoardMocks.withVehiclesAndExits(
            vehicles = listOf(vehicle),
            exits = listOf(exit)
        )
        val state = GameState(board = board)

        val result = useCase(state, "a", distance = 1) // short of the exit

        assertEquals(false, result.isSolved)
        assertEquals(1, result.board.vehicles.size)
    }
}