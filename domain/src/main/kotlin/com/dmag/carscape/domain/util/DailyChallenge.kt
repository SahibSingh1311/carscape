package com.dmag.carscape.domain.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object DailyChallenge {
    private val LAUNCH_DATE = LocalDate.of(2026,9, 1)

    fun currentLevelNumber(zoneId: ZoneId = ZoneId.systemDefault()) : Int {
        val today = LocalDate.now(zoneId)
        val daysSinceLaunch = ChronoUnit.DAYS.between(LAUNCH_DATE, today)
        return (daysSinceLaunch+1).coerceAtLeast(1).toInt()
    }

    fun todayEpochDay(zoneId: ZoneId = ZoneId.systemDefault()): Long = LocalDate.now(zoneId).toEpochDay()

    fun secondsUntilNextDay(zoneId: ZoneId = ZoneId.systemDefault()): Long {
        val now = LocalDateTime.now(zoneId)
        val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
        return ChronoUnit.SECONDS.between(now, midnight)
    }
}