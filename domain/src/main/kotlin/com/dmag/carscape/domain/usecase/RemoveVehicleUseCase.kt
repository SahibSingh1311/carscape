package com.dmag.carscape.domain.usecase

import com.dmag.carscape.domain.model.GameState
import javax.inject.Inject

class RemoveVehicleUseCase @Inject constructor() {
    operator fun invoke(state: GameState, vehicleId: String): GameState {
        val newVehicles = state.board.vehicles.filterNot { it.id == vehicleId }
        if (newVehicles.size == state.board.vehicles.size) return state
        return state.copy(
            board = state.board.copy(vehicles = newVehicles),
            isSolved = newVehicles.isEmpty()
        )
    }
}