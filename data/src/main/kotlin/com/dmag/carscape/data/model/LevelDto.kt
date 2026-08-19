package com.dmag.carscape.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VehicleDto(
    val id: String = "",
    val orientation: String = "",
    val length: Int = 0,
    val headRow: Int = 0,
    val headCol: Int = 0,
    val colorIndex: Int = 0
)

@Serializable
data class ExitDto(
    val row: Int = 0,
    val col: Int = 0,
    val orientation: String = "",
    val colorIndex: Int? = null
)
@Serializable
data class LevelDto(
    val rows: Int = 0,
    val cols: Int = 0,
    val vehicles: List<VehicleDto> = emptyList(),
    val exits: List<ExitDto> = emptyList(),
    val timeLimitSeconds: Int = 60,
    val coinReward: Int = 10
)