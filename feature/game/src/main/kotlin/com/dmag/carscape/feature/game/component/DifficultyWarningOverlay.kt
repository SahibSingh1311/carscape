package com.dmag.carscape.feature.game.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dmag.carscape.core.designsystem.theme.LuckiestGuy
import com.dmag.carscape.domain.model.LevelDifficulty
import kotlinx.coroutines.delay

@Composable
fun DifficultyWarningOverlay(
    difficulty: LevelDifficulty,
    onFinished: () -> Unit
) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        repeat(3) {
            alpha.animateTo(1f, animationSpec = tween(180))
            delay(280)
            alpha.animateTo(0f, animationSpec = tween(180))
            delay(160)
        }
        onFinished()
    }

    val label = when (difficulty) {
        LevelDifficulty.VERY_HARD -> "VERY HARD"
        LevelDifficulty.HARD -> "HARD"
        LevelDifficulty.NORMAL -> ""
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Swallow every touch/drag while the warning plays — nothing beneath should be reachable
            .pointerInput(Unit) {
                awaitEachGesture {
                    while (true) {
                        val event = awaitPointerEvent()
                        event.changes.forEach { it.consume() }
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = alpha.value)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color.Red.copy(alpha = 0.55f)),
                        radius = 1400f
                    )
                )
        )

        Text(
            text = "⚠️ $label ⚠️",
            color = Color.White,
            fontFamily = LuckiestGuy,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer(alpha = alpha.value)
        )
    }
}