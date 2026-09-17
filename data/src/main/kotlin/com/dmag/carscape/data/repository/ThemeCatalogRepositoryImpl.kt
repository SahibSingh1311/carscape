package com.dmag.carscape.data.repository

import com.dmag.carscape.core.common.DispatcherProvider
import com.dmag.carscape.domain.model.BoardThemeDefinition
import com.dmag.carscape.domain.model.VehicleThemeDefinition
import com.dmag.carscape.domain.repository.ThemeCatalogRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ThemeCatalogRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val dispatchers: DispatcherProvider
) : ThemeCatalogRepository {

    override suspend fun getBoardThemes(): List<BoardThemeDefinition> = withContext(dispatchers.io) {
        try {
            firestore.collection("board_themes").get().await()
                .mapNotNull { it.toObject(BoardThemeDefinition::class.java) }
        } catch (e: Exception) {
            emptyList() // graceful — UI falls back to just the built-in Default theme
        }
    }

    override suspend fun getVehicleThemes(): List<VehicleThemeDefinition> = withContext(dispatchers.io) {
        try {
            firestore.collection("vehicle_themes").get().await()
                .mapNotNull { it.toObject(VehicleThemeDefinition::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}