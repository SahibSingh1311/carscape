package com.dmag.carscape.domain.usecase

import com.dmag.carscape.domain.model.GameState
import com.dmag.carscape.domain.model.Orientation
import com.dmag.carscape.domain.usecase.mocks.BoardMocks
import com.dmag.carscape.domain.usecase.mocks.VehicleMocks
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoveVehicleUseCaseTest {

    private val useCase = RemoveVehicleUseCase()

    @Test
    fun `removes the specified vehicle from the board`() {
        val target = VehicleMocks.smallCar("a", Orientation.HORIZONTAL)
        val other = VehicleMocks.sedan("b", Orientation.VERTICAL)
        val board = BoardMocks.withVehicles(target, other)
        val state = GameState(board = board)

        val result = useCase(state, "a")

        assertEquals(1, result.board.vehicles.size)
        assertEquals("b", result.board.vehicles.first().id)
    }

    @Test
    fun `removing an unknown vehicle id returns unchanged state`() {
        val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL)
        val board = BoardMocks.withVehicles(vehicle)
        val state = GameState(board = board)

        val result = useCase(state, "nonexistent")

        assertEquals(state, result)
    }

    @Test
    fun `removing the only remaining vehicle solves the level`() {
        val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL)
        val board = BoardMocks.withVehicles(vehicle)
        val state = GameState(board = board)

        val result = useCase(state, "a")

        assertTrue(result.isSolved)
        assertTrue(result.board.vehicles.isEmpty())
    }

    @Test
    fun `removing one of several vehicles does not solve the level`() {
        val target = VehicleMocks.smallCar("a", Orientation.HORIZONTAL)
        val other = VehicleMocks.sedan("b", Orientation.VERTICAL)
        val board = BoardMocks.withVehicles(target, other)
        val state = GameState(board = board)

        val result = useCase(state, "a")

        assertEquals(false, result.isSolved)
    }
}