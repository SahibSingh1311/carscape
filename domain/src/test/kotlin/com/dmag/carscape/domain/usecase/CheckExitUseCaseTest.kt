package com.dmag.carscape.domain.usecase

import com.dmag.carscape.domain.model.Cell
import com.dmag.carscape.domain.model.Orientation
import com.dmag.carscape.domain.usecase.mocks.BoardMocks
import com.dmag.carscape.domain.usecase.mocks.VehicleMocks
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckExitUseCaseTest {

    private val useCase = CheckExitUseCase()

    // ---------- thickness 1 ----------

    @Test
    fun `vehicle exits when edge lands on matching right exit`() {
        // length 2, head at col 4 → occupies col 4–5 on a 6-col board (on right edge)
        val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL, Cell(2, 4))
        val exit = BoardMocks.rightExit(row = 2, cols = 6, colorIndex = 0)
        val board = BoardMocks.withVehiclesAndExits(
            vehicles = listOf(vehicle),
            exits = listOf(exit)
        )

        assertTrue(useCase(board, vehicle).exited)
    }

    @Test
    fun `vehicle does not exit through mismatched color`() {
        val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL, Cell(2, 4))
        val exit = BoardMocks.rightExit(row = 2, cols = 6, colorIndex = 1)
        val board = BoardMocks.withVehiclesAndExits(
            vehicles = listOf(vehicle),
            exits = listOf(exit)
        )

        assertFalse(useCase(board, vehicle).exited)
    }

    @Test
    fun `vehicle does not exit through mismatched orientation`() {
        val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL, Cell(2, 4))
        val exit = BoardMocks.bottomExit(col = 5, rows = 6, colorIndex = 0)
        val board = BoardMocks.withVehiclesAndExits(
            vehicles = listOf(vehicle),
            exits = listOf(exit)
        )

        assertFalse(useCase(board, vehicle).exited)
    }

    @Test
    fun `vehicle not on border does not exit`() {
        val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL, Cell(2, 0))
        val exit = BoardMocks.rightExit(row = 2, cols = 6, colorIndex = 0)
        val board = BoardMocks.withVehiclesAndExits(
            vehicles = listOf(vehicle),
            exits = listOf(exit)
        )

        assertFalse(useCase(board, vehicle).exited)
    }

    @Test
    fun `vertical vehicle exits through matching bottom exit`() {
        // length 3 vertical, head row 3 → occupies rows 3–5 on 6-row board
        val vehicle = VehicleMocks.sedan("a", Orientation.VERTICAL, Cell(3, 2))
        val exit = BoardMocks.bottomExit(col = 2, rows = 6, colorIndex = 0)
        val board = BoardMocks.withVehiclesAndExits(
            vehicles = listOf(vehicle),
            exits = listOf(exit)
        )

        assertTrue(useCase(board, vehicle).exited)
    }

    @Test
    fun `vehicle exits through matching left exit`() {
        val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL, Cell(1, 0))
        val exit = BoardMocks.leftExit(row = 1, colorIndex = 0)
        val board = BoardMocks.withVehiclesAndExits(
            vehicles = listOf(vehicle),
            exits = listOf(exit)
        )

        assertTrue(useCase(board, vehicle).exited)
    }

    @Test
    fun `vehicle exits through matching top exit`() {
        val vehicle = VehicleMocks.sedan("a", Orientation.VERTICAL, Cell(0, 3))
        val exit = BoardMocks.topExit(col = 3, colorIndex = 0)
        val board = BoardMocks.withVehiclesAndExits(
            vehicles = listOf(vehicle),
            exits = listOf(exit)
        )

        assertTrue(useCase(board, vehicle).exited)
    }

    // ---------- thickness 2 (bus / suv) ----------

    @Test
    fun `bus exits through matching 2-wide right exit`() {
        // 2×6 bus, head (1,0) on 6-col board → occupies rows 1–2, cols 0–5 (on right edge)
        val bus = VehicleMocks.bus(head = Cell(1, 0), colorIndex = 4)
        val exit = BoardMocks.rightWide(row = 1, cols = 6, colorIndex = 4)
        val board = BoardMocks.withVehiclesAndExits(
            vehicles = listOf(bus),
            exits = listOf(exit)
        )

        assertTrue(useCase(board, bus).exited)
    }

    @Test
    fun `bus does not exit through 1-wide exit`() {
        val bus = VehicleMocks.bus(head = Cell(1, 0), colorIndex = 4)
        val exit = BoardMocks.rightExit(row = 1, cols = 6, thickness = 1, colorIndex = 4)
        val board = BoardMocks.withVehiclesAndExits(
            vehicles = listOf(bus),
            exits = listOf(exit)
        )

        assertFalse(useCase(board, bus).exited)
    }

    @Test
    fun `bus does not exit when only partially aligned with 2-wide exit`() {
        // Exit starts at row 0, bus occupies rows 1–2 → not exact match
        val bus = VehicleMocks.bus(head = Cell(1, 0), colorIndex = 4)
        val exit = BoardMocks.rightWide(row = 0, cols = 6, colorIndex = 4)
        val board = BoardMocks.withVehiclesAndExits(
            vehicles = listOf(bus),
            exits = listOf(exit)
        )

        assertFalse(useCase(board, bus).exited)
    }

    @Test
    fun `vertical suv exits through matching 2-wide bottom exit`() {
        // 2×4 vertical suv, head (2, 3) on 6-row board → rows 2–5, cols 3–4
        val suv = VehicleMocks.suv(
            orientation = Orientation.VERTICAL,
            head = Cell(2, 3),
            colorIndex = 2
        )
        val exit = BoardMocks.bottomWide(col = 3, rows = 6, colorIndex = 2)
        val board = BoardMocks.withVehiclesAndExits(
            vehicles = listOf(suv),
            exits = listOf(exit)
        )

        assertTrue(useCase(board, suv).exited)
    }

    @Test
    fun `bus exits through matching 2-wide left exit`() {
        val bus = VehicleMocks.bus(head = Cell(2, 0), colorIndex = 4)
        val exit = BoardMocks.leftWide(row = 2, colorIndex = 4)
        val board = BoardMocks.withVehiclesAndExits(
            vehicles = listOf(bus),
            exits = listOf(exit),
            rows = 8,
            cols = 8,
        )

        assertTrue(useCase(board, bus).exited)
    }
}