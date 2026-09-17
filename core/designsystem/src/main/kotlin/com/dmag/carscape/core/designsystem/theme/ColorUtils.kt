package com.dmag.carscape.core.designsystem.theme

import androidx.compose.ui.graphics.Color

fun parseHexColor(hex: String, fallback: Color = Color.Gray): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: IllegalArgumentException) {
    fallback
}