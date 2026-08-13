package com.dmag.carscape.feature.game

import com.dmag.carscape.domain.model.Board
import com.dmag.carscape.domain.model.GameMode

sealed interface GameUiState {
    data object Loading : GameUiState

    data class Success(
        val board: Board,
        val moves: Int,
        val isSolved: Boolean,
        val levelNumber: Int,
        val mode: GameMode,
        val timeRemainingSeconds: Int?
    ) : GameUiState

    data class DailyLocked(val secondsRemaining: Long) : GameUiState

    data class TimeUp(val levelNumber: Int) : GameUiState

    data class NoMoreLevels(val lastLevelNumber: Int) : GameUiState
}