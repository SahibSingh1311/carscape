package com.dmag.carscape.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmag.carscape.core.designsystem.theme.LuckiestGuy

@Composable
fun ChunkyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    backgroundColor: Color = Color(0xFFF4C430),
    enabled: Boolean = true
) {

    // Desaturate toward gray when disabled, rather than just dimming opacity —
    // this keeps text/edges crisp instead of looking washed-out/translucent.
    val effectiveColor = if (enabled) {
        backgroundColor
    } else {
        val gray = (backgroundColor.red + backgroundColor.green + backgroundColor.blue) / 3f
        Color(
            red = androidx.compose.ui.util.lerp(backgroundColor.red, gray, 0.75f),
            green = androidx.compose.ui.util.lerp(backgroundColor.green, gray, 0.75f),
            blue = androidx.compose.ui.util.lerp(backgroundColor.blue, gray, 0.75f)
        )
    }

    val lighter = Color(
        red = (effectiveColor.red + 0.18f).coerceAtMost(1f),
        green = (effectiveColor.green + 0.14f).coerceAtMost(1f),
        blue = (effectiveColor.blue + 0.10f).coerceAtMost(1f)
    )
    val darker = Color(
        red = (effectiveColor.red - 0.18f).coerceAtLeast(0f),
        green = (effectiveColor.green - 0.22f).coerceAtLeast(0f),
        blue = (effectiveColor.blue - 0.25f).coerceAtLeast(0f)
    )
    val deepest = Color(
        red = (effectiveColor.red - 0.28f).coerceAtLeast(0f),
        green = (effectiveColor.green - 0.32f).coerceAtLeast(0f),
        blue = (effectiveColor.blue - 0.35f).coerceAtLeast(0f)
    )

    val shape = RoundedCornerShape(16.dp)
    val thickness = 6.dp          // how "chunky" the button feels

    Box(
        modifier = modifier
            .height(60.dp)
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // 1. Soft outer shadow (gives depth)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 4.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = shape,
                    ambientColor = Color.Black.copy(alpha = 0.25f),
                    spotColor = Color.Black.copy(alpha = 0.35f)
                )
        )

        // 2. Bottom layer (the thick side wall)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = thickness)
                .background(deepest, shape)
        )

        // 3. Main face of the button
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(lighter, effectiveColor, darker)
                    ),
                    shape = shape
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (enabled) 0.55f else 0.2f),
                            Color.Transparent,
                            deepest.copy(alpha = 0.6f)
                        )
                    ),
                    shape = shape
                )
        )

        // 4. Top highlight (makes it look shiny / plastic)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.92f)
                .height(18.dp)
                .padding(top = 4.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (enabled) 0.45f else 0.15f),
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
        )

        // 5. Text
        if(supportingText?.isNotBlank() == true) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = text,
                    fontFamily = LuckiestGuy,
                    color = Color.White,
                    fontSize = 24.sp,
                    letterSpacing = 1.sp,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0x66000000),
                            offset = Offset(0f, 2f),
                            blurRadius = 2f
                        )
                    )
                )
                Text(
                    text = supportingText,
                    color = Color.White,
                    fontSize = 16.sp,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0x66000000),
                            offset = Offset(0f, 2f),
                            blurRadius = 2f
                        )
                    )
                )
            }
        } else
        Text(
            text = text,
            fontFamily = LuckiestGuy,
            color = Color.White,
            fontSize = 24.sp,
            letterSpacing = 1.sp,
            style = TextStyle(
                shadow = Shadow(
                    color = Color(0x66000000),
                    offset = Offset(0f, 2f),
                    blurRadius = 2f
                )
            )
        )
    }
}
