package com.dmag.carscape.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.dmag.carscape.core.common.DispatcherProvider
import com.dmag.carscape.data.BuildConfig
import com.dmag.carscape.domain.model.PowerUpInventory
import com.dmag.carscape.domain.model.PowerUpType
import com.dmag.carscape.domain.model.Wallet
import com.dmag.carscape.domain.repository.AuthRepository
import com.dmag.carscape.domain.repository.WalletRepository
import com.dmag.carscape.domain.repository.WalletRepository.Companion.HEART_REGEN_SECONDS
import com.dmag.carscape.domain.repository.WalletRepository.Companion.MAX_HEARTS
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions.merge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private val COINS_KEY = intPreferencesKey("wallet_coins")
private val HEARTS_KEY = intPreferencesKey("wallet_hearts")
private val LAST_HEART_LOST_AT_KEY = longPreferencesKey("wallet_last_heart_lost_at")
private val HAMMER_KEY = intPreferencesKey("wallet_powerup_hammer")
private val FREEZE_KEY = intPreferencesKey("wallet_powerup_freeze")
private val ADD_TIME_KEY = intPreferencesKey("wallet_pwoerup_add_time")

@Singleton
class WalletRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    private val dispatchers: DispatcherProvider
): WalletRepository{
    // Background sync work shouldn't tie up any single caller's coroutine scope
    private val syncScope = CoroutineScope(SupervisorJob() + dispatchers.io)

    init {
        syncScope.launch { refreshHeartRegen() }
    }

    override val wallet: Flow<Wallet> = dataStore.data.map { prefs ->
        val w = Wallet(
            coins = prefs[COINS_KEY] ?: 0,
            hearts = prefs[HEARTS_KEY] ?: MAX_HEARTS,
            powerUps = PowerUpInventory(
                hammer = prefs[HAMMER_KEY] ?: 0,
                freeze = prefs[FREEZE_KEY] ?: 0,
                addTime = prefs[ADD_TIME_KEY] ?: 0
            )
        )

        if (BuildConfig.DEBUG) {
            android.util.Log.d("WalletRepo", "wallet emission: coins=${w.coins}, hearts=${w.hearts}")
        }
        w
    }

    override suspend fun addCoins(amount: Int) {
        dataStore.edit { prefs->
            prefs[COINS_KEY] = (prefs[COINS_KEY] ?: 0) + amount
        }
        syncToFirestore()
    }

    override suspend fun spendCoins(amount: Int): Boolean {
        val current = dataStore.data.first()[COINS_KEY] ?: 0
        if (current < amount) return false
        dataStore.edit {
            prefs-> prefs[COINS_KEY] = current - amount
        }
        syncToFirestore()
        return true
    }

    override suspend fun loseHeart() {
        dataStore.edit { prefs ->
            val current = prefs[HEARTS_KEY] ?: MAX_HEARTS
            prefs[HEARTS_KEY] = (current-1).coerceAtLeast(0)
        }
        syncToFirestore()
    }

    override suspend fun addHeart() {
        dataStore.edit { prefs ->
            val current = prefs[HEARTS_KEY] ?: MAX_HEARTS
            prefs[HEARTS_KEY] = (current+1).coerceAtMost(0)
        }
        syncToFirestore()
    }

    override suspend fun refreshHeartRegen() {
        dataStore.edit { prefs -> applyHeartRegen(prefs) }
    }

    /**
     * Catches up hearts regenerated while the app was closed or idle.
     * Advances the stored timestamp by exactly the number of intervals consumed,
     * so partial progress toward the next heart isn't lost or reset.
     */
    private fun applyHeartRegen(prefs: MutablePreferences) {
        val hearts = prefs[HEARTS_KEY] ?: MAX_HEARTS
        if (hearts >= MAX_HEARTS) {
            prefs.remove(LAST_HEART_LOST_AT_KEY)
            return
        }

        val lastLostAt = prefs[LAST_HEART_LOST_AT_KEY] ?: run {
            // Below max but no timestamp recorded (shouldn't normally happen) — start counting from now
            prefs[LAST_HEART_LOST_AT_KEY] = nowEpochSeconds()
            return
        }

        val elapsed = nowEpochSeconds() - lastLostAt
        val intervalsPassed = (elapsed / HEART_REGEN_SECONDS).toInt()
        if (intervalsPassed <= 0) return

        val newHearts = (hearts + intervalsPassed).coerceAtMost(MAX_HEARTS)
        val heartsActuallyAdded = newHearts - hearts
        prefs[HEARTS_KEY] = newHearts

        if (newHearts >= MAX_HEARTS) {
            prefs.remove(LAST_HEART_LOST_AT_KEY)
        } else {
            prefs[LAST_HEART_LOST_AT_KEY] = lastLostAt + (heartsActuallyAdded * HEART_REGEN_SECONDS)
        }
    }

    private fun nowEpochSeconds(): Long = System.currentTimeMillis() / 1000

    override suspend fun addPowerUp(type: PowerUpType, count: Int) {
        val key = keyFor(type)
        dataStore.edit { prefs ->
            prefs[key] = (prefs[key] ?: 0) + count
        }
        syncToFirestore()
    }

    override suspend fun consumePowerUp(type: PowerUpType): Boolean {
        val key = keyFor(type)
        val current = dataStore.data.first()[key] ?: 0
        if (current <= 0) return false
        dataStore.edit { prefs -> prefs[key] = current - 1 }
        syncToFirestore()
        return true
    }

    private fun keyFor(type: PowerUpType) = when (type) {
        PowerUpType.HAMMER -> HAMMER_KEY
        PowerUpType.FREEZE -> FREEZE_KEY
        PowerUpType.ADD_TIME -> ADD_TIME_KEY
    }

    private fun syncToFirestore() {
        syncScope.launch {
            try{
                val uid = authRepository.getPlayerId()
                val current = wallet.first()
                firestore.collection("users").document(uid)
                    .set(
                        mapOf(
                            "coins" to current.coins,
                            "hearts" to current.hearts,
                            "powerUps" to mapOf(
                                "hammer" to current.powerUps.hammer,
                                "freeze" to current.powerUps.freeze,
                                "addTime" to current.powerUps.addTime
                            )
                        ),
                        merge()
                    )
                    .await()
            } catch (e: Exception) {
                // Sync failures are non-fatal — local DataStore remains correct,
                // and the next successful sync will catch this value up.
            }
        }
    }
}