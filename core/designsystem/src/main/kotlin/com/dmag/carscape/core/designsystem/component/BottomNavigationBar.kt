package com.dmag.carscape.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmag.carscape.core.designsystem.R
import com.dmag.carscape.core.designsystem.theme.LuckiestGuy

enum class BottomNavItem(
    val title: String,
    @DrawableRes val iconRes: Int
) {
    Marketplace("Marketplace", R.drawable.ic_marketplace),   // ← replace with your real drawables
    Play("Play", R.drawable.ic_play),
    Inventory("Inventory", R.drawable.ic_inventory)
}

private val TOTAL_HEIGHT = 110.dp
private val BAR_HEIGHT = 88.dp
private val RAISED_HEIGHT = 104.dp // taller than BAR_HEIGHT so it pokes above; both share the same bottom edge

@Composable
fun GameBottomNavigation(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val barColor = Color(0xFFC17A3A)          // main brown
    val selectedTabColor = Color(0xFFD9924A)  // slightly lighter for selected
    val dividerColor = Color(0xFF8B5A2B)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .height(TOTAL_HEIGHT)                     // total height including the raised part
    ) {
        // Main brown bar (bottom part)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(BAR_HEIGHT)
                .background(
                    color = barColor,
                )
                .border(
                    width = 2.dp,
                    color = Color(0xFF8B5A2B),
                )
        )

        // The three tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(TOTAL_HEIGHT),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            BottomNavItem.entries.forEach { item ->
                val isSelected = item == selectedItem

                GameBottomNavItem(
                    item = item,
                    isSelected = isSelected,
                    selectedTabColor = selectedTabColor,
                    onClick = { onItemSelected(item) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun GameBottomNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    selectedTabColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null,          // we handle the visual ourselves
                onClick = onClick
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Raised selected background (the part that sticks out)
        if (isSelected) {
            // No manual offset at all — height + BottomCenter alignment does the work.
            // This box's bottom edge is guaranteed identical to the bar's bottom edge.
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(RAISED_HEIGHT)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(18.dp),
                        ambientColor = Color.Black.copy(alpha = 0.25f),
                        spotColor = Color.Black.copy(alpha = 0.3f)
                    )
                    .background(selectedTabColor, RoundedCornerShape(18.dp))
                    .border(2.5.dp, Color(0xFF8B5A2B), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center // icon+label centered INSIDE the pill, not pushed to its bottom
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.title,
                        modifier = Modifier.size(60.dp)
                            .offset(y = (-12).dp)
                    )
                    Text(
                        text = item.title.uppercase(),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = LuckiestGuy,
                        letterSpacing = 0.5.sp,
                        style = TextStyle(
                            shadow = Shadow(Color.Black.copy(alpha = 0.4f), Offset(0f, 1.5f), 2f)
                        )
                    )
                }
            }
        } else {
            // Unselected items sit centered within just the bar's height,
            // so they visually align with the bar, not the taller raised pill
            Box(
                modifier = Modifier.height(BAR_HEIGHT),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = item.title,
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }
}