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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dmag.carscape.core.designsystem.theme.SurfaceDark

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onModeSelected: (GameMode) -> Unit,
    onMarketplaceClick: () -> Unit,
    onInventoryClick: () -> Unit,
    coins: Int = 0,      // placeholder until wallet system exists
    hearts: Int = 5       // placeholder until hearts system exists
) {
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
                            Text(" $hearts")
                        }
                        Text("CarScape")
                        Text("🪙 $coins")
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
            ModeCard(mode = GameMode.DAILY, onClick = { onModeSelected(GameMode.DAILY) })
            ModeCard(mode = GameMode.TIMED, onClick = { onModeSelected(GameMode.TIMED) })
            ModeCard(mode = GameMode.CASUAL, onClick = { onModeSelected(GameMode.CASUAL) })
        }
    }
}

@Composable
private fun ModeCard(mode: GameMode, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Text(text = mode.displayName)
        Text(text = mode.description)
    }
}