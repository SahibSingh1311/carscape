package com.dmag.carscape.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dmag.carscape.core.designsystem.component.BottomNavItem
import com.dmag.carscape.core.designsystem.component.ChunkyButton
import com.dmag.carscape.core.designsystem.component.GameBottomNavigation
import com.dmag.carscape.core.designsystem.theme.LuckiestGuy
import com.dmag.carscape.domain.model.GameMode

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onModeSelected: (GameMode) -> Unit,
    onMarketplaceClick: () -> Unit,
    onInventoryClick: () -> Unit,
    adBubble: @Composable () -> Unit = {},
    noHeartsDialog: @Composable (onDismiss: () -> Unit, onHeartEarned: () -> Unit) -> Unit = { _, _ -> },
    viewModel: HomeViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsState()
    var pendingMode by remember { mutableStateOf<GameMode?>(null) }

    fun tryStartMode(mode: GameMode) {
        if (state.hearts > 0) {
            onModeSelected(mode)
        } else {
            pendingMode = mode
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = com.dmag.carscape.core.designsystem.R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.25f),
                            Color.Black.copy(alpha = 0.55f),
                            Color.Black.copy(alpha = 0.80f)
                        )
                    )
                )
        )

        pendingMode?.let { mode ->
            noHeartsDialog(
                { pendingMode = null },
                { pendingMode = null; onModeSelected(mode) }
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row {
                                Icon(Icons.Filled.Favorite, contentDescription = "Hearts", tint = Color.Red)
                                Text(text = " ${state.hearts}", fontFamily = LuckiestGuy)
                            }
                            Text(text = "CarScape", fontFamily = LuckiestGuy)
                            Row {
                                Text(text = "💎 ${state.diamonds}", fontFamily = LuckiestGuy)
                                Text(text = "🪙 ${state.coins}", fontFamily = LuckiestGuy)
                            }
                        }
                    }
                )
            },
            bottomBar = {
                GameBottomNavigation(
                    selectedItem = BottomNavItem.Play, // Home screen = the Play tab
                    onItemSelected = { item ->
                        when (item) {
                            BottomNavItem.Marketplace -> onMarketplaceClick()
                            BottomNavItem.Inventory -> onInventoryClick()
                            BottomNavItem.Play -> {} // already here, no-op
                        }
                    }
                )
//                NavigationBar {
//                    NavigationBarItem(
//                        selected = false,
//                        onClick = onMarketplaceClick,
//                        icon = {
//                            Icon(
//                                Icons.Filled.ShoppingCart,
//                                contentDescription = "Marketplace"
//                            )
//                        },
//                        label = { Text("Marketplace") }
//                    )
//                    NavigationBarItem(
//                        selected = true,
//                        onClick = { /* already home */ },
//                        icon = { Icon(Icons.Filled.Home, contentDescription = "Play") },
//                        label = { Text("Play") }
//                    )
//                    NavigationBarItem(
//                        selected = false,
//                        onClick = onInventoryClick,
//                        icon = { Icon(Icons.Filled.Email, contentDescription = "Inventory") },
//                        label = { Text("Inventory") }
//                    )
//                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(48.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                ) {
                    ChunkyButton(
                        text = if (state.isDailyLocked) "DAILY" else "DAILY CHALLENGE",
                        supportingText = if (state.isDailyLocked) "Next in ${state.dailyCountdownText}" else null,
                        backgroundColor = Color(0xFF4CC94F),
                        enabled = !state.isDailyLocked,
                        onClick = { tryStartMode(GameMode.DAILY)  },
                    )
                    ChunkyButton(
                        text = "TIMED MODE",
                        backgroundColor = Color(0xFFFFC145),
                        onClick = { tryStartMode(GameMode.TIMED) },
                    )
                    ChunkyButton(
                        text = "CASUAL MODE",
                        backgroundColor = Color(0xFF4FA3E0),
                        onClick = { tryStartMode(GameMode.CASUAL) },
                    )
                }
                Box(modifier = Modifier.align(Alignment.CenterStart)) {
                    adBubble()
                }
            }
        }
    }
}

//@Composable
//private fun ModeCard(title: String, subtitle: String, emoji: String, enabled: Boolean, onClick: () -> Unit) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .shadow(elevation = if (enabled) 8.dp else 0.dp, shape = RoundedCornerShape(18.dp))
//            .clip(RoundedCornerShape(18.dp))
//            .background(Brush.verticalGradient(listOf(WoodLight, WoodDark)))
//            .border(
//                width = 3.dp,
//                brush = Brush.verticalGradient(listOf(GoldBright, GoldDeep)),
//                shape = RoundedCornerShape(18.dp)
//            )
//            .alpha(if (enabled) 1f else 0.45f)
//            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
//            .padding(16.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Box(
//            modifier = Modifier
//                .size(52.dp)
//                .clip(CircleShape)
//                .background(Brush.radialGradient(listOf(GoldBright, GoldDeep))),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(text = emoji, fontSize = 26.sp)
//        }
//        Spacer(modifier = Modifier.width(16.dp))
//        Column {
//            Text(text = title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = OnSurfaceLight)
//            Text(text = subtitle, fontSize = 13.sp, color = OnSurfaceLight.copy(alpha = 0.75f))
//        }
//    }
//}