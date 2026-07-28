package com.dmag.carscape.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.dmag.carscape.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val UNLOCKED_LEVEL_KEY = intPreferencesKey("unlocked_level")

class ProgressRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ProgressRepository {

    override suspend fun getUnlockedLevel(): Int =
        dataStore.data.first()[UNLOCKED_LEVEL_KEY] ?: 1

    override suspend fun setUnlockedLevel(level: Int) {
        dataStore.edit { prefs -> prefs[UNLOCKED_LEVEL_KEY] = level }
    }
}