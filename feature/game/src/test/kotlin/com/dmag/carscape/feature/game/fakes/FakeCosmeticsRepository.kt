package com.dmag.carscape.feature.game.fakes

import com.dmag.carscape.domain.model.CosmeticsState
import com.dmag.carscape.domain.repository.CosmeticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class FakeCosmeticsRepository(initial: CosmeticsState = CosmeticsState()) : CosmeticsRepository {
    private val _cosmetics = MutableStateFlow(initial)
    override val cosmetics: StateFlow<CosmeticsState> get() = _cosmetics

    override suspend fun unlockBoardTheme(themeId: String) {
        _cosmetics.update { it.copy(ownedBoardThemeIds = it.ownedBoardThemeIds + themeId) }
    }
    override suspend fun unlockVehicleTheme(themeId: String) {
        _cosmetics.update { it.copy(ownedVehicleThemeIds = it.ownedVehicleThemeIds + themeId) }
    }
    override suspend fun equipBoardTheme(themeId: String): Boolean {
        if (themeId !in _cosmetics.value.ownedBoardThemeIds) return false
        _cosmetics.update { it.copy(equippedBoardThemeId = themeId) }
        return true
    }
    override suspend fun equipVehicleTheme(themeId: String): Boolean {
        if (themeId !in _cosmetics.value.ownedVehicleThemeIds) return false
        _cosmetics.update { it.copy(equippedVehicleThemeId = themeId) }
        return true
    }
}