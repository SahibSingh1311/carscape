package com.dmag.carscape.domain.repository

import com.dmag.carscape.domain.model.Board
import com.dmag.carscape.domain.model.GameMode

interface LevelRepository {
    suspend fun getLevel(mode: GameMode, levelNumber: Int): Board
    suspend fun getLevelCount(mode: GameMode): Int
}