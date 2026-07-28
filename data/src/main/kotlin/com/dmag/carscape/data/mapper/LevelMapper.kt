package com.dmag.carscape.data.mapper

import com.dmag.carscape.data.model.LevelDto
import com.dmag.carscape.domain.model.Board
import com.dmag.carscape.domain.model.Cell
import com.dmag.carscape.domain.model.ExitGate
import com.dmag.carscape.domain.model.Orientation
import com.dmag.carscape.domain.model.Vehicle

fun LevelDto.toDomain(): Board = Board(
    rows = rows,
    cols = cols,
    vehicles = vehicles.map { dto ->
        Vehicle(
            id = dto.id,
            orientation = Orientation.valueOf(dto.orientation),
            length = dto.length,
            head = Cell(dto.headRow, dto.headCol),
            colorIndex = dto.colorIndex
        )
    },
    exits = exits.map { dto ->
        ExitGate(
            cell = Cell(dto.row, dto.col),
            orientation = Orientation.valueOf(dto.orientation),
            colorIndex = dto.colorIndex
        )
    }
)