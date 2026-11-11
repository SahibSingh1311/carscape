package com.dmag.carscape.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmag.carscape.core.designsystem.theme.GoldBright
import com.dmag.carscape.core.designsystem.theme.GoldDeep
import com.dmag.carscape.core.designsystem.theme.LuckiestGuy
import com.dmag.carscape.domain.model.PowerUpType
import com.dmag.carscape.powerups.PowerUpDisplay

@Composable
fun InventoryCard(
    display: PowerUpDisplay,
    count: Int
) {
    val hasAny = count > 0
    val glowColors = when (display.type) {
        PowerUpType.HAMMER -> listOf(Color(0xFF4A2A1A), Color(0xFF7A4A2A))
        PowerUpType.FREEZE -> listOf(Color(0xFF1A3A4A), Color(0xFF2A6A8A))
        PowerUpType.ADD_TIME -> listOf(Color(0xFF3A2A4A), Color(0xFF6A3A8A))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .alpha(if (hasAny) 1f else 0.55f)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(glowColors))
            .border(
                width = 3.dp,
                brush = Brush.verticalGradient(listOf(GoldBright, GoldDeep)),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = display.iconRes),
                contentDescription = display.label,
                modifier = Modifier.size(64.dp)
            )

            // Owned-count badge, bottom-right corner of the art frame
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (hasAny) GoldDeep else Color(0xFF3A3A3A))
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "x$count", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.size(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = display.label.uppercase(),
                color = Color.White,
                fontFamily = LuckiestGuy,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = display.description,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp,
                lineHeight = 15.sp
            )
        }
    }
}