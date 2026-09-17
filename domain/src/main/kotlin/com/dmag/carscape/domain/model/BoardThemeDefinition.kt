package com.dmag.carscape.domain.model

data class BoardThemeDefinition(
    val id: String = "",
    val name: String = "",
    val price: Int = 0,
    val tileColorA: String = "#2E2119",
    val tileColorB: String = "#3E2E22",
    val borderColor: String = "#E8A93B"
)

data class VehicleThemeDefinition(
    val id: String = "",
    val name: String = "",
    val price: Int = 0,
    val tintMultiplier: Float = 1f
)

val DEFAULT_BOARD_THEME = BoardThemeDefinition(
    id = "default", name = "Classic Asphalt", price = 0,
    tileColorA = "#35354F", tileColorB = "#44446A", borderColor = "#E8A93B"
)
val DEFAULT_VEHICLE_THEME = VehicleThemeDefinition(id = "default", name = "Classic", price = 0)