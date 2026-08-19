package com.dmag.carscape.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dmag.carscape.core.designsystem.theme.SurfaceDark
import com.dmag.carscape.domain.model.GameMode

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onModeSelected: (GameMode) -> Unit,
    onMarketplaceClick: () -> Unit,
    onInventoryClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Favorite, contentDescription = "Hearts")
                            Text(" ${state.hearts}")
                        }
                        Text("CarScape")
                        Text("🪙 ${state.coins}")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = onMarketplaceClick,
                    icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Marketplace") },
                    label = { Text("Marketplace") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* already home */ },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Play") },
                    label = { Text("Play") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onInventoryClick,
                    icon = { Icon(Icons.Filled.Email, contentDescription = "Inventory") },
                    label = { Text("Inventory") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            ModeCard(
                title = "Daily Challenge",
                subtitle = if (state.isDailyLocked) "Next in ${state.dailyCountdownText}" else "One new puzzle every day",
                enabled = !state.isDailyLocked,
                onClick = { onModeSelected(GameMode.DAILY) })
            ModeCard(
                title = "Timed Mode",
                subtitle = "Race the clock, earn coins",
                enabled = true,
                onClick = { onModeSelected(GameMode.TIMED) })
            ModeCard(
                title = "Casual Mode",
                subtitle = "No timer, no pressure",
                enabled = true,
                onClick = { onModeSelected(GameMode.CASUAL) })
        }
    }
}

@Composable
private fun ModeCard(title: String, subtitle: String, enabled: Boolean,onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .alpha(if (enabled) 1f else 0.5f)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(20.dp)
    ) {
        Text(text = title)
        Text(text = subtitle)
    }
}