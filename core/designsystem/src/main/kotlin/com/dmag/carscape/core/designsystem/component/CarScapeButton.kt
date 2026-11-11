package com.dmag.carscape.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dmag.carscape.core.designsystem.theme.ActionGreen
import com.dmag.carscape.core.designsystem.theme.GoldBright
import com.dmag.carscape.core.designsystem.theme.LuckiestGuy

@Composable
fun CarScapeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(ActionGreen, ActionGreen.copy(alpha = 0.8f))))
            .border(2.dp, GoldBright, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 32.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontFamily = LuckiestGuy, color = Color.White, fontWeight = FontWeight.Bold)
    }
}