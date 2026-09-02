package com.dmag.carscape.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.dmag.carscape.marketplace.MarketplaceItem

@Composable
fun MarketplaceCard(
    item: MarketplaceItem,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    val glowColors = when (item.type) {
        com.dmag.carscape.domain.model.PowerUpType.HAMMER -> listOf(Color(0xFF4A2A1A), Color(0xFF7A4A2A))
        com.dmag.carscape.domain.model.PowerUpType.FREEZE -> listOf(Color(0xFF1A3A4A), Color(0xFF2A6A8A))
        com.dmag.carscape.domain.model.PowerUpType.ADD_TIME -> listOf(Color(0xFF3A2A4A), Color(0xFF6A3A8A))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .alpha(if (canAfford) 1f else 0.5f)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(glowColors))
            .border(
                width = 3.dp,
                brush = Brush.verticalGradient(listOf(GoldBright, GoldDeep)),
                shape = RoundedCornerShape(16.dp)
            )
            .then(if (canAfford) Modifier.clickable(onClick = onBuy) else Modifier)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Item art, framed like a CR card portrait
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = item.iconRes),
                contentDescription = item.label,
                modifier = Modifier.size(64.dp)
            )
        }

        Box(modifier = Modifier.size(12.dp)) // spacer

        // Name + price stacked, right side
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Text(
                text = item.label.uppercase(),
                color = Color.White,
                fontFamily = LuckiestGuy,
                fontSize = 18.sp
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .border(1.5.dp, GoldDeep, RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(text = "🪙 ${item.price}", color = GoldBright, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}