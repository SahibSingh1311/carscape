package com.dmag.carscape.feature.game.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dmag.carscape.core.designsystem.theme.ExitGlow
import com.dmag.carscape.core.designsystem.theme.SurfaceDark
import com.dmag.carscape.domain.model.GameMode
import com.dmag.carscape.domain.model.PowerUpInventory

@Composable
fun PowerUpBar(
    powerUps: PowerUpInventory,
    mode: GameMode,
    isHammerModeActive: Boolean,
    onHammerClick: () -> Unit,
    onFreezeClick: () -> Unit,
    onAddTimeClick: () -> Unit
) {
    Row {
        PowerUpButton(
            label = "🔨 ${powerUps.hammer}",
            enabled = powerUps.hammer > 0,
            highlighted = isHammerModeActive,
            onClick = onHammerClick
        )
        if (mode == GameMode.TIMED) {
            PowerUpButton(
                label = "❄️ ${powerUps.freeze}",
                enabled = powerUps.freeze > 0,
                onClick = onFreezeClick
            )
            PowerUpButton(
                label = "⏱️ ${powerUps.addTime}",
                enabled = powerUps.addTime > 0,
                onClick = onAddTimeClick
            )
        }
    }
}

@Composable
private fun PowerUpButton(
    label: String,
    enabled: Boolean,
    highlighted: Boolean = false,
    onClick: () -> Unit
) {
    androidx.compose.material3.Text(
        text = label,
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (highlighted) ExitGlow else SurfaceDark)
            .alpha(if (enabled) 1f else 0.4f)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(12.dp)
    )
}