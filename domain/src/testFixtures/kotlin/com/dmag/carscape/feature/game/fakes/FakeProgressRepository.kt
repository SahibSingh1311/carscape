package com.dmag.carscape.feature.game.fakes

import com.dmag.carscape.domain.model.GameMode
import com.dmag.carscape.domain.repository.ProgressRepository

class FakeProgressRepository : ProgressRepository {
    private val unlockedLevels = mutableMapOf<GameMode, Int>()
    private var lastDailyCompletionEpochDay: Long? = null

    override suspend fun getUnlockedLevel(mode: GameMode): Int = unlockedLevels[mode] ?: 1
    override suspend fun setUnlockedLevel(mode: GameMode, level: Int) { unlockedLevels[mode] = level }
    override suspend fun getLastDailyCompletionEpochDay(): Long? = lastDailyCompletionEpochDay
    override suspend fun setLastDailyCompletionEpochDay(epochDay: Long) { lastDailyCompletionEpochDay = epochDay }
}