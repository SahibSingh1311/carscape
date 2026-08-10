package com.dmag.carscape.data.repository

import android.util.Log
import com.dmag.carscape.data.BuildConfig
import com.dmag.carscape.core.common.DispatcherProvider
import com.dmag.carscape.data.mapper.toDomain
import com.dmag.carscape.data.model.LevelDto
import com.dmag.carscape.domain.model.Board
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

    override suspend fun getLevel(levelNumber: Int): Board = withContext(dispatchers.io) {
        val snapshot = firestore.collection("levels")
            .document(levelNumber.toString())
            .get()
            .await()

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Fetched level $levelNumber raw data: ${snapshot.data}")
        }

        val dto = snapshot.toObject(LevelDto::class.java)
            ?: throw NoSuchElementException("Level $levelNumber not found")

        dto.toDomain()
    }

    override suspend fun getLevelCount(): Int = withContext(dispatchers.io) {
        firestore.collection("levels").get().await().size()
    }
}