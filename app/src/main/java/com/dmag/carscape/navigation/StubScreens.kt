package com.dmag.carscape.navigation

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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dmag.carscape.core.designsystem.theme.SurfaceDark
import com.dmag.carscape.inventory.InventoryViewModel
import com.dmag.carscape.marketplace.MarketplaceItem
import com.dmag.carscape.marketplace.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    onInventoryClick: () -> Unit,
    onHomeClick: () -> Unit,
    viewModel: MarketplaceViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.purchaseMessage) {
        if (state.purchaseMessage != null) {
            kotlinx.coroutines.delay(1500)
            viewModel.dismissMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Marketplace  🪙 ${state.wallet.coins}") })
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* already marketplace */ },
                    icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Marketplace") },
                    label = { Text("Marketplace") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onHomeClick ,
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            state.purchaseMessage?.let {
                Text(text = it)
            }
            state.items.forEach { item ->
                MarketplaceRow(
                    item = item,
                    canAfford = state.wallet.coins >= item.price,
                    onBuy = { viewModel.buy(item) }
                )
            }
        }
    }
}

@Composable
private fun MarketplaceRow(
    item: MarketplaceItem,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .alpha(if (canAfford) 1f else 0.5f)
            .clickable(enabled = canAfford, onClick = onBuy)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "${item.emoji} ${item.label}")
        Text(text = "🪙 ${item.price}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onMarketplaceClick: () -> Unit,
    onHomeClick: () -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val wallet by viewModel.wallet.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Inventory  🪙 ${wallet.coins}") })
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
                    selected = false,
                    onClick = onHomeClick ,
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Play") },
                    label = { Text("Play") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* already inventory */ },
                    icon = { Icon(Icons.Filled.Email, contentDescription = "Inventory") },
                    label = { Text("Inventory") }
                )
            }
        }
    ) {padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InventoryRow(
                "🔨 Hammer",
                "Instantly removes any vehicle from the board",
                wallet.powerUps.hammer
            )
            InventoryRow(
                "❄️ Freeze",
                "Pauses the Timed countdown for 8 seconds",
                wallet.powerUps.freeze
            )
            InventoryRow(
                "⏱️ Add Time",
                "Adds 15 seconds to the Timed countdown",
                wallet.powerUps.addTime
            )
        }
    }
}

@Composable
private fun InventoryRow(title: String, description: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title)
            Text(text = description)
        }
        Text(text = "x$count")
    }
}