package com.dmag.carscape.data.repository

import android.content.Context
import com.dmag.carscape.core.common.DispatcherProvider
import com.dmag.carscape.data.mapper.toDomain
import com.dmag.carscape.data.model.LevelDto
import com.dmag.carscape.domain.model.Board
import com.dmag.carscape.domain.repository.LevelRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class LevelRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatcherProvider
) : LevelRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getLevel(levelNumber: Int): Board = withContext(dispatchers.io) {
        val fileName = "levels/level_$levelNumber.json"
        val jsonText = context.assets.open(fileName).bufferedReader().use { it.readText() }
        json.decodeFromString<LevelDto>(jsonText).toDomain()
    }

    override suspend fun getLevelCount(): Int = withContext(dispatchers.io) {
        context.assets.list("levels")?.size ?: 0
    }
}