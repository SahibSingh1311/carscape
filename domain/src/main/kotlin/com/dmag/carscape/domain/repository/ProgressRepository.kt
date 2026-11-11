package com.dmag.carscape.domain.repository

import com.dmag.carscape.domain.model.GameMode

interface ProgressRepository {
    suspend fun getUnlockedLevel(mode: GameMode): Int
    suspend fun setUnlockedLevel(mode: GameMode, level: Int)
    suspend fun getLastDailyCompletionEpochDay(): Long?
    suspend fun setLastDailyCompletionEpochDay(epochDay: Long)
}