package com.dmag.carscape.domain.repository

import com.dmag.carscape.domain.model.BoardThemeDefinition
import com.dmag.carscape.domain.model.VehicleThemeDefinition

interface ThemeCatalogRepository {
    suspend fun getBoardThemes(): List<BoardThemeDefinition>
    suspend fun getVehicleThemes(): List<VehicleThemeDefinition>
}