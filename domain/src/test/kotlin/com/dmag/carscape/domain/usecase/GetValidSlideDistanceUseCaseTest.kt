package com.dmag.carscape.domain.usecase

import com.dmag.carscape.domain.model.Cell
import com.dmag.carscape.domain.model.Orientation
import com.dmag.carscape.domain.usecase.mocks.BoardMocks
import com.dmag.carscape.domain.usecase.mocks.VehicleMocks
import org.junit.Assert.assertEquals
import org.junit.Test

class GetValidSlideDistanceUseCaseTest {

    private val useCase = GetValidSlideDistanceUseCase()

    @Test
    fun `horizontal vehicle can slide right until board edge`() {
        val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL)
        val board = BoardMocks.withVehicles(vehicle)

        val distance = useCase(board, vehicle, SlideDirection.RIGHT)

        assertEquals(4, distance) // head can move from col0 to col4 (tail lands on col5, last column)
    }

    @Test
    fun `horizontal vehicle cannot slide left past board edge`() {
        val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL)
        val board = BoardMocks.withVehicles(vehicle)

        val distance = useCase(board, vehicle, SlideDirection.LEFT)

        assertEquals(0, distance) // already at the left edge
    }

    @Test
    fun `vehicle is blocked by another vehicle in its path`() {
        val moving = VehicleMocks.smallCar("a", Orientation.HORIZONTAL)
        val blocker = VehicleMocks.sedan("b", Orientation.VERTICAL, Cell(0, 3))
        val board = BoardMocks.withVehicles(moving,blocker)

        val distance = useCase(board, moving, SlideDirection.RIGHT)

        assertEquals(1, distance) // stops right before the blocker's column
    }

    @Test
    fun `perpendicular direction returns zero (axis lock enforced)`() {
        val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL)
        val board = BoardMocks.withVehicles(vehicle)

        val distance = useCase(board, vehicle, SlideDirection.UP)

        assertEquals(0, distance) // horizontal vehicle can't move vertically
    }

    @Test
    fun `vertical vehicle can slide down until board edge`() {
        val vehicle = VehicleMocks.sedan("a", Orientation.VERTICAL, Cell(0, 2))
        val board = BoardMocks.withVehicles(vehicle)

        val distance = useCase(board, vehicle, SlideDirection.DOWN)

        assertEquals(3, distance) // head moves from row0 to row3, tail lands on row5
    }
}