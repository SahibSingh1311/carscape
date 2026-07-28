package com.dmag.carscape.domain.usecase

import com.dmag.carscape.domain.model.GameState
import com.dmag.carscape.domain.model.Orientation
import javax.inject.Inject

class MoveVehicleUseCase @Inject constructor(
    private val getValidSlideDistance: GetValidSlideDistanceUseCase,
    private val checkExit: CheckExitUseCase
) {
    operator fun invoke(state: GameState, vehicleId: String, direction: SlideDirection): GameState {
        val vehicle = state.board.vehicles.find { it.id == vehicleId } ?: return state
        val distance = getValidSlideDistance(state.board, vehicle, direction)
        if (distance == 0) return state

        val movedVehicle = vehicle.copy(
            head = if (vehicle.orientation == Orientation.VERTICAL)
                vehicle.head.copy(row = vehicle.head.row + distance)
            else
                vehicle.head.copy(col = vehicle.head.col + distance)
        )

        val exitResult = checkExit(state.board, movedVehicle)
        val newVehicles = if (exitResult.exited) {
            state.board.vehicles.filterNot { it.id == vehicleId }
        } else {
            state.board.vehicles.map { if (it.id == vehicleId) movedVehicle else it }
        }

        val newBoard = state.board.copy(vehicles = newVehicles)
        return state.copy(
            board = newBoard,
            moves = state.moves + 1,
            isSolved = newVehicles.isEmpty()
        )
    }
}