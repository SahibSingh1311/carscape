package com.dmag.carscape.domain.repository

import com.dmag.carscape.domain.model.Board

interface LevelRepository {
    suspend fun getLevel(levelNumber: Int): Board
    suspend fun getLevelCount(): Int
}