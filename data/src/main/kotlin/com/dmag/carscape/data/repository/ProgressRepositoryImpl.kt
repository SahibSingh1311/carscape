package com.dmag.carscape.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.dmag.carscape.core.common.DispatcherProvider
import com.dmag.carscape.domain.model.GameMode
import com.dmag.carscape.domain.repository.AuthRepository
import com.dmag.carscape.domain.repository.ProgressRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private val LAST_DAILY_COMPLETION_KEY = longPreferencesKey("last_daily_completion_epoch_day")

class ProgressRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    private val dispatchers: DispatcherProvider
) : ProgressRepository {

    private val syncScope = CoroutineScope(SupervisorJob() + dispatchers.io)

    private fun keyFor(mode: GameMode) = intPreferencesKey("unlocked_level_${mode.name.lowercase()}")
    private fun firestoreFieldFor(mode: GameMode) = "unlockedLevel${mode.name.lowercase().replaceFirstChar { it.uppercase() }}"

    override suspend fun getUnlockedLevel(mode: GameMode): Int =
        dataStore.data.first()[keyFor(mode)] ?: 1

    override suspend fun setUnlockedLevel(mode: GameMode, level: Int) {
        dataStore.edit { prefs -> prefs[keyFor(mode)] = level }
        syncToFirestore()
    }

    override suspend fun getLastDailyCompletionEpochDay(): Long? =
        dataStore.data.first()[LAST_DAILY_COMPLETION_KEY]

    override suspend fun setLastDailyCompletionEpochDay(epochDay: Long) {
        dataStore.edit { prefs -> prefs[LAST_DAILY_COMPLETION_KEY] = epochDay }
        syncToFirestore()
    }

    /**
     * Called once at app startup. Pulls remote progress and takes the higher
     * value per field — ensures a fresh install (or cleared local data) doesn't
     * silently lose progress that already exists in Firestore, while also never
     * regressing Firestore if local happens to be further ahead.
     */
    suspend fun reconcileWithRemote() {
        try {
            val uid = authRepository.getPlayerId()
            val snapshot = firestore.collection("users").document(uid).get().await()
            if (!snapshot.exists()) return

            dataStore.edit { prefs ->
                GameMode.values().forEach { mode ->
                    val remoteLevel = snapshot.getLong(firestoreFieldFor(mode))?.toInt() ?: 1
                    val localLevel = prefs[keyFor(mode)] ?: 1
                    if (remoteLevel > localLevel) {
                        prefs[keyFor(mode)] = remoteLevel
                    }
                }
                val remoteDailyCompletion = snapshot.getLong("lastDailyCompletionEpochDay")
                val localDailyCompletion = prefs[LAST_DAILY_COMPLETION_KEY]
                if (remoteDailyCompletion != null &&
                    (localDailyCompletion == null || remoteDailyCompletion > localDailyCompletion)
                ) {
                    prefs[LAST_DAILY_COMPLETION_KEY] = remoteDailyCompletion
                }
            }
        } catch (e: Exception) {
            // Offline or first-ever launch — local defaults remain correct, nothing to reconcile yet.
        }
    }

    private fun syncToFirestore() {
        syncScope.launch {
            try {
                val uid = authRepository.getPlayerId()
                val prefs = dataStore.data.first()
                val update = mutableMapOf<String, Any>()
                GameMode.values().forEach { mode ->
                    update[firestoreFieldFor(mode)] = prefs[keyFor(mode)] ?: 1
                }
                prefs[LAST_DAILY_COMPLETION_KEY]?.let { update["lastDailyCompletionEpochDay"] = it }

                firestore.collection("users").document(uid)
                    .set(update, SetOptions.merge())
                    .await()
            } catch (e: Exception) {
                // Non-fatal — local DataStore remains correct; next successful write will catch up.
            }
        }
    }
}