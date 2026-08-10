package com.dmag.carscape.feature.game

import com.dmag.carscape.domain.model.Board

sealed interface GameUiState {
    data object Loading : GameUiState

    data class Success(
        val board: Board,
        val moves: Int,
        val isSolved: Boolean,
        val levelNumber: Int
    ) : GameUiState

    data class NoMoreLevels(val lastLevelNumber: Int) : GameUiState
}