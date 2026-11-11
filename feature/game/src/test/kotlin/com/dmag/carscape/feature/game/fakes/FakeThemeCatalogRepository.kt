package com.dmag.carscape.feature.game.fakes

import com.dmag.carscape.domain.model.BoardThemeDefinition
import com.dmag.carscape.domain.model.VehicleThemeDefinition
import com.dmag.carscape.domain.repository.ThemeCatalogRepository

class FakeThemeCatalogRepository(
    private val boardThemes: List<BoardThemeDefinition> = emptyList(),
    private val vehicleThemes: List<VehicleThemeDefinition> = emptyList()
) : ThemeCatalogRepository {
    override suspend fun getBoardThemes(): List<BoardThemeDefinition> = boardThemes
    override suspend fun getVehicleThemes(): List<VehicleThemeDefinition> = vehicleThemes
}
