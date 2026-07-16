package com.dmag.carscape.domain.repository

interface ProgressRepository {
    suspend fun getUnlockedLevel(): Int
    suspend fun setUnlockedLevel(level: Int)
}