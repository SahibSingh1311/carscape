package com.dmag.carscape.feature.game.fakes

import com.dmag.carscape.domain.model.Board
import com.dmag.carscape.domain.model.GameMode
import com.dmag.carscape.domain.repository.LevelRepository

class FakeLevelRepository : LevelRepository {
    private val levels = mutableMapOf<Pair<GameMode, Int>, Board>()

    fun putLevel(mode: GameMode, levelNumber: Int, board: Board) {
        levels[mode to levelNumber] = board
    }

    override suspend fun getLevel(mode: GameMode, levelNumber: Int): Board =
        levels[mode to levelNumber] ?: throw NoSuchElementException("No fake level for $mode/$levelNumber")

    override suspend fun getLevelCount(mode: GameMode): Int =
        levels.keys.count { it.first == mode }
}