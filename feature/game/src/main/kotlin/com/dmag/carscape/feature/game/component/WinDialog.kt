package com.dmag.carscape.feature.game.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dmag.carscape.core.designsystem.component.CarScapeButton
import com.dmag.carscape.core.designsystem.theme.ExitGlow
import com.dmag.carscape.core.designsystem.theme.LuckiestGuy

@Composable
fun WinDialog(
    moves: Int,
    coinsEarned: Int? = null,
    diamondsEarned: Int? = null,
    onNextLevel: (() -> Unit)?,
    onRetry: (() -> Unit)?
) {
    Dialog(onDismissRequest = { /* force a choice, no dismiss-on-outside-tap */ }) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Level Complete!",
                fontSize = 24.sp,
                fontFamily = LuckiestGuy,
                fontWeight = FontWeight.Bold,
                color = ExitGlow,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Solved in $moves moves", fontFamily = LuckiestGuy, color = Color.White)
            if (coinsEarned != null) {
                Text(text = "🪙 +$coinsEarned coins", fontFamily = LuckiestGuy, color = Color.White)
            }
            if (diamondsEarned != null && diamondsEarned > 0) Text(text = "💎 +$diamondsEarned diamonds", fontFamily = LuckiestGuy, color = Color.White)
            Spacer(modifier = Modifier.height(20.dp))
            if (onNextLevel != null) {
                CarScapeButton(text = "Next Level", onClick = onNextLevel)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (onRetry != null)
            CarScapeButton(text = if (onNextLevel != null) "Retry" else "Back to Home", onClick = onRetry)
        }
    }
}