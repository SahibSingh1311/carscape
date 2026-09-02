package com.dmag.carscape.powerups

import androidx.annotation.DrawableRes
import com.dmag.carscape.core.designsystem.R
import com.dmag.carscape.domain.model.PowerUpType

data class PowerUpDisplay(
    val type: PowerUpType,
    val label: String,
    @DrawableRes val iconRes: Int,
    val description: String
)

val PowerUpDisplays = listOf(
    PowerUpDisplay(PowerUpType.HAMMER, "Hammer", R.drawable.powerup_hammer, "Instantly removes any vehicle from the board"),
    PowerUpDisplay(PowerUpType.FREEZE, "Freeze", R.drawable.powerup_freeze, "Pauses the Timed countdown for 8 seconds"),
    PowerUpDisplay(PowerUpType.ADD_TIME, "Add Time", R.drawable.powerup_add_time, "Adds 15 seconds to the Timed countdown")
)