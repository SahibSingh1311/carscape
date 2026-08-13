package com.dmag.carscape.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.dmag.carscape.domain.model.GameMode
import com.dmag.carscape.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val LAST_DAILY_COMPLETION_KEY = longPreferencesKey("last_daily_completion_epoch_day")

class ProgressRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ProgressRepository {

    private fun keyFor(mode: GameMode) = intPreferencesKey("unlocked_level_${mode.name.lowercase()}")

    override suspend fun getUnlockedLevel(mode: GameMode): Int =
        dataStore.data.first()[keyFor(mode)] ?: 1

    override suspend fun setUnlockedLevel(mode: GameMode, level: Int) {
        dataStore.edit { prefs -> prefs[keyFor(mode)] = level }
    }

    override suspend fun getLastDailyCompletionEpochDay(): Long? =
        dataStore.data.first()[LAST_DAILY_COMPLETION_KEY]

    override suspend fun setLastDailyCompletionEpochDay(epochDay: Long) {
        dataStore.edit { prefs -> prefs[LAST_DAILY_COMPLETION_KEY] = epochDay }
    }
}