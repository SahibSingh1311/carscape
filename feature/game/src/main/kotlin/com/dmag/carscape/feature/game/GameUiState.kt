package com.dmag.carscape.feature.game

import com.dmag.carscape.domain.model.Board
import com.dmag.carscape.domain.model.GameMode
import com.dmag.carscape.domain.model.PowerUpInventory

sealed interface GameUiState {
    data object Loading : GameUiState

    data class Success(
        val board: Board,
        val moves: Int,
        val isSolved: Boolean,
        val levelNumber: Int,
        val mode: GameMode,
        val timeRemainingSeconds: Int?,
        val powerUps: PowerUpInventory = PowerUpInventory(),
        val hearts: Int = 0,
        val isHammerModeActive: Boolean = false,
        val showDifficultyWarning: Boolean = false
    ) : GameUiState

    data class DailyLocked(val secondsRemaining: Long) : GameUiState

    data class TimeUp(val levelNumber: Int) : GameUiState

    data class NoMoreLevels(val lastLevelNumber: Int) : GameUiState

    data class MovesExceeded(val levelNumber: Int, val hearts: Int = 0,) : GameUiState
}