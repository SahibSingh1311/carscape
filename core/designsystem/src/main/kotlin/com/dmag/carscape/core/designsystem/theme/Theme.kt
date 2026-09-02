package com.dmag.carscape.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush

val CarScapeBackgroundBrush = Brush.verticalGradient(
    colors = listOf(RoyalDeep, RoyalMid, RoyalDeep)
)

private val CarScapeColorScheme = darkColorScheme(
    background = RoyalDeep,
    surface = SurfaceCard,
    onSurface = OnSurfaceLight,
    primary = GoldBright,
    secondary = ActionRed
)

@Composable
fun CarScapeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CarScapeColorScheme,
        typography = CarScapeTypography,
        content = content
    )
}