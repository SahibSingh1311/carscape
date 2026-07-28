package com.dmag.carscape.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VehicleDto(
    val id: String,
    val orientation: String,
    val length: Int,
    val headRow: Int,
    val headCol: Int,
    val colorIndex: Int
)

@Serializable
data class ExitDto(
    val row: Int,
    val col: Int,
    val orientation: String,
    val colorIndex: Int? = null
)
@Serializable
data class LevelDto(
    val rows: Int,
    val cols: Int,
    val vehicles: List<VehicleDto>,
    val exits: List<ExitDto>
)