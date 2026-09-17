package com.dmag.carscape.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.dmag.carscape.core.common.DispatcherProvider
import com.dmag.carscape.domain.model.CosmeticsState
import com.dmag.carscape.domain.repository.AuthRepository
import com.dmag.carscape.domain.repository.CosmeticsRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private val OWNED_BOARD_THEMES_KEY = stringSetPreferencesKey("owned_board_themes")
private val OWNED_VEHICLE_THEMES_KEY = stringSetPreferencesKey("owned_vehicle_themes")
private val EQUIPPED_BOARD_THEME_KEY = stringPreferencesKey("equipped_board_theme")
private val EQUIPPED_VEHICLE_THEME_KEY = stringPreferencesKey("equipped_vehicle_theme")

@Singleton
class CosmeticsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    private val dispatchers: DispatcherProvider
) : CosmeticsRepository {

    private val syncScope = CoroutineScope(SupervisorJob() + dispatchers.io)

    override val cosmetics: Flow<CosmeticsState> = dataStore.data.map { prefs ->
        CosmeticsState(
            ownedBoardThemeIds = (prefs[OWNED_BOARD_THEMES_KEY] ?: setOf("default")).ifEmpty { setOf("default") },
            ownedVehicleThemeIds = (prefs[OWNED_VEHICLE_THEMES_KEY] ?: setOf("default")).ifEmpty { setOf("default") },
            equippedBoardThemeId = prefs[EQUIPPED_BOARD_THEME_KEY] ?: "default",
            equippedVehicleThemeId = prefs[EQUIPPED_VEHICLE_THEME_KEY] ?: "default"
        )
    }

    override suspend fun unlockBoardTheme(themeId: String) {
        dataStore.edit { prefs ->
            prefs[OWNED_BOARD_THEMES_KEY] = (prefs[OWNED_BOARD_THEMES_KEY] ?: setOf("default")) + themeId
        }
        syncToFirestore()
    }

    override suspend fun unlockVehicleTheme(themeId: String) {
        dataStore.edit { prefs ->
            prefs[OWNED_VEHICLE_THEMES_KEY] = (prefs[OWNED_VEHICLE_THEMES_KEY] ?: setOf("default")) + themeId
        }
        syncToFirestore()
    }

    override suspend fun equipBoardTheme(themeId: String): Boolean {
        val owned = dataStore.data.first()[OWNED_BOARD_THEMES_KEY] ?: setOf("default")
        if (themeId !in owned) return false
        dataStore.edit { prefs -> prefs[EQUIPPED_BOARD_THEME_KEY] = themeId }
        syncToFirestore()
        return true
    }

    override suspend fun equipVehicleTheme(themeId: String): Boolean {
        val owned = dataStore.data.first()[OWNED_VEHICLE_THEMES_KEY] ?: setOf("default")
        if (themeId !in owned) return false
        dataStore.edit { prefs -> prefs[EQUIPPED_VEHICLE_THEME_KEY] = themeId }
        syncToFirestore()
        return true
    }

    private fun syncToFirestore() {
        syncScope.launch {
            try {
                val uid = authRepository.getPlayerId()
                val current = cosmetics.first()
                firestore.collection("users").document(uid)
                    .set(
                        mapOf(
                            "ownedBoardThemes" to current.ownedBoardThemeIds.toList(),
                            "ownedVehicleThemes" to current.ownedVehicleThemeIds.toList(),
                            "equippedBoardTheme" to current.equippedBoardThemeId,
                            "equippedVehicleTheme" to current.equippedVehicleThemeId
                        ),
                        SetOptions.merge()
                    )
                    .await()
            } catch (e: Exception) { /* non-fatal */ }
        }
    }
}