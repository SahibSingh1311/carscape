package com.dmag.carscape.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CarScapeColorScheme = darkColorScheme(
    background = BackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceLight,
    primary = ExitGlow
)

@Composable
fun CarScapeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CarScapeColorScheme, // dark-only for now — traffic games read better on dark backgrounds
        typography = CarScapeTypography,
        content = content
    )
}