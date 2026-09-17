package com.dmag.carscape.domain.repository

import com.dmag.carscape.domain.model.CosmeticsState
import kotlinx.coroutines.flow.Flow

interface CosmeticsRepository {
    val cosmetics: Flow<CosmeticsState>

    suspend fun unlockBoardTheme(themeId: String)
    suspend fun unlockVehicleTheme(themeId: String)
    suspend fun equipBoardTheme(themeId: String): Boolean
    suspend fun equipVehicleTheme(themeId: String): Boolean
}