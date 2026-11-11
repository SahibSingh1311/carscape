package com.dmag.carscape.domain.model

data class CosmeticsState(
    val ownedBoardThemeIds: Set<String> = setOf("default"),
    val ownedVehicleThemeIds: Set<String> = setOf("default"),
    val equippedBoardThemeId: String = "default",
    val equippedVehicleThemeId: String = "default"
)
