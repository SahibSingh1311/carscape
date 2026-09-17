package com.dmag.carscape.feature.game.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmag.carscape.core.designsystem.R
import com.dmag.carscape.core.designsystem.theme.GoldBright
import com.dmag.carscape.core.designsystem.theme.GoldDeep
import com.dmag.carscape.core.designsystem.theme.WoodDark
import com.dmag.carscape.core.designsystem.theme.WoodLight
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
    Row(modifier = Modifier.padding(8.dp)) {
        PowerUpChip(
            iconRes = R.drawable.powerup_hammer,
            count = powerUps.hammer,
            highlighted = isHammerModeActive,
            onClick = onHammerClick
        )
        if (mode == GameMode.TIMED) {
            PowerUpChip(
                iconRes = R.drawable.powerup_freeze,
                count = powerUps.freeze,
                onClick = onFreezeClick
            )
            PowerUpChip(
                iconRes = R.drawable.powerup_add_time,
                count = powerUps.addTime,
                onClick = onAddTimeClick
            )
        }
    }
}

@Composable
private fun PowerUpChip(
    iconRes: Int,
    count: Int,
    highlighted: Boolean = false,
    onClick: () -> Unit
) {
    val enabled = count > 0

    Box(
        modifier = Modifier
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .size(60.dp) // slightly larger so badge has room
            .alpha(if (enabled) 1f else 0.45f)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        // 1. Circular chip (clipped) – icon only
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(52.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        if (highlighted) listOf(GoldBright, GoldDeep)
                        else listOf(WoodLight, WoodDark)
                    )
                )
                .border(
                    width = 2.dp,
                    color = if (highlighted) Color.White else GoldDeep,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
        }

        // 2. Count badge – NOT inside the clipped circle
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(22.dp)
                .clip(CircleShape)
                .background(GoldDeep)
                .border(1.5.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}