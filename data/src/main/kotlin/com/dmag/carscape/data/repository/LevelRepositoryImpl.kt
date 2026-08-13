package com.dmag.carscape.data.repository

import android.util.Log
import com.dmag.carscape.data.BuildConfig
import com.dmag.carscape.core.common.DispatcherProvider
import com.dmag.carscape.data.mapper.toDomain
import com.dmag.carscape.data.model.LevelDto
import com.dmag.carscape.domain.model.Board
import com.dmag.carscape.domain.model.GameMode
import com.dmag.carscape.domain.repository.LevelRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "LevelRepository"

class LevelRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val dispatchers: DispatcherProvider
) : LevelRepository {

    private fun collectionFor(mode: GameMode): String = when (mode) {
        GameMode.DAILY -> "levels_daily"
        GameMode.TIMED -> "levels_timed"
        GameMode.CASUAL -> "levels_casual"
    }

    override suspend fun getLevel(mode: GameMode, levelNumber: Int): Board = withContext(dispatchers.io) {
        val snapshot = firestore.collection(collectionFor(mode))
            .document(levelNumber.toString())
            .get()
            .await()

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Fetched ${mode.name} level $levelNumber raw data: ${snapshot.data}")
        }

        val dto = snapshot.toObject(LevelDto::class.java)
            ?: run {
                if(BuildConfig.DEBUG) Log.e(TAG, "${mode.name} level $levelNumber not found")
                throw NoSuchElementException("Level $levelNumber not found")
            }

        dto.toDomain()
    }

    override suspend fun getLevelCount(mode: GameMode): Int = withContext(dispatchers.io) {
        firestore.collection(collectionFor(mode)).get().await().size()
    }
}