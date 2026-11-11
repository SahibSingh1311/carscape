package com.dmag.carscape.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dmag.carscape.core.designsystem.component.BottomNavItem
import com.dmag.carscape.core.designsystem.component.GameBottomNavigation
import com.dmag.carscape.core.designsystem.component.MarketplaceCard
import com.dmag.carscape.core.designsystem.component.ShimmerCard
import com.dmag.carscape.core.designsystem.theme.LuckiestGuy
import com.dmag.carscape.core.designsystem.theme.SurfaceDark
import com.dmag.carscape.domain.model.PowerUpType
import com.dmag.carscape.inventory.InventoryCard
import com.dmag.carscape.inventory.InventoryViewModel
import com.dmag.carscape.marketplace.MarketplaceViewModel
import com.dmag.carscape.powerups.PowerUpDisplays

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

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = com.dmag.carscape.core.designsystem.R.drawable.ic_background),
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
                            Color.Black.copy(alpha = 0.10f),
                            Color.Black.copy(alpha = 0.25f),
                            Color.Black.copy(alpha = 0.55f)
                        )
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(Color(0xFFC17A3A) ),
                    title = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp)
                        ) {
                            Text(
                                text = "Marketplace",
                                fontFamily = LuckiestGuy
                            )
                            Text(
                                text = "🪙 ${state.wallet.coins}",
                                fontFamily = LuckiestGuy
                            )
                        }
                    },
                    modifier = Modifier.shadow(
                        elevation = 10.dp,                    // ← adjust this value
                        spotColor = Color.Black.copy(alpha = 0.35f),
                        ambientColor = Color.Black.copy(alpha = 0.25f)
                    ))
            },
            bottomBar = {
                GameBottomNavigation(
                    selectedItem = BottomNavItem.Marketplace, // Home screen = the Play tab
                    onItemSelected = { item ->
                        when (item) {
                            BottomNavItem.Marketplace -> {}
                            BottomNavItem.Inventory -> onInventoryClick()
                            BottomNavItem.Play -> onHomeClick() // already here, no-op
                        }
                    }
                )
                //            NavigationBar {
                //                NavigationBarItem(
                //                    selected = true,
                //                    onClick = { /* already marketplace */ },
                //                    icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Marketplace") },
                //                    label = { Text("Marketplace") }
                //                )
                //                NavigationBarItem(
                //                    selected = false,
                //                    onClick = onHomeClick ,
                //                    icon = { Icon(Icons.Filled.Home, contentDescription = "Play") },
                //                    label = { Text("Play") }
                //                )
                //                NavigationBarItem(
                //                    selected = false,
                //                    onClick = onInventoryClick,
                //                    icon = { Icon(Icons.Filled.Email, contentDescription = "Inventory") },
                //                    label = { Text("Inventory") }
                //                )
                //            }
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
                if (state.isLoading) {
                    repeat(3) {
                        ShimmerCard()
                    }
                } else {
                    state.items.forEach { item ->
                        MarketplaceCard(
                            item = item,
                            canAfford = state.wallet.coins >= item.price,
                            onBuy = { viewModel.buy(item) }
                        )
                    }
                }
            }
        }
    }
}

//@Composable
//private fun MarketplaceRow(
//    item: MarketplaceItem,
//    canAfford: Boolean,
//    onBuy: () -> Unit
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(16.dp))
//            .background(SurfaceDark)
//            .alpha(if (canAfford) 1f else 0.5f)
//            .clickable(enabled = canAfford, onClick = onBuy)
//            .padding(20.dp),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(text = "${item.emoji} ${item.label}")
//        Text(text = "🪙 ${item.price}")
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onMarketplaceClick: () -> Unit,
    onHomeClick: () -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val wallet by viewModel.wallet.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = com.dmag.carscape.core.designsystem.R.drawable.ic_background),
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
                            Color.Black.copy(alpha = 0.10f),
                            Color.Black.copy(alpha = 0.25f),
                            Color.Black.copy(alpha = 0.55f)
                        )
                    )
                )
        )
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(Color(0xFFC17A3A)),
                    title = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp)
                        ) {
                            Text(
                                text = "Inventory",
                                fontFamily = LuckiestGuy
                            )
                            Text(
                                text = "🪙 ${wallet.coins}",
                                fontFamily = LuckiestGuy
                            )
                        }
                    },
                    modifier = Modifier.shadow(
                        elevation = 10.dp,                    // ← adjust this value
                        spotColor = Color.Black.copy(alpha = 0.35f),
                        ambientColor = Color.Black.copy(alpha = 0.25f)
                    )
                )
            },
            bottomBar = {
                GameBottomNavigation(
                    selectedItem = BottomNavItem.Inventory, // Home screen = the Play tab
                    onItemSelected = { item ->
                        when (item) {
                            BottomNavItem.Marketplace -> onMarketplaceClick()
                            BottomNavItem.Inventory -> {}
                            BottomNavItem.Play -> onHomeClick() // already here, no-op
                        }
                    }
                )
                //            NavigationBar {
                //                NavigationBarItem(
                //                    selected = false,
                //                    onClick = onMarketplaceClick,
                //                    icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Marketplace") },
                //                    label = { Text("Marketplace") }
                //                )
                //                NavigationBarItem(
                //                    selected = false,
                //                    onClick = onHomeClick ,
                //                    icon = { Icon(Icons.Filled.Home, contentDescription = "Play") },
                //                    label = { Text("Play") }
                //                )
                //                NavigationBarItem(
                //                    selected = true,
                //                    onClick = { /* already inventory */ },
                //                    icon = { Icon(Icons.Filled.Email, contentDescription = "Inventory") },
                //                    label = { Text("Inventory") }
                //                )
                //            }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PowerUpDisplays.forEach { display ->
                    val count = when (display.type) {
                        PowerUpType.HAMMER -> wallet.powerUps.hammer
                        PowerUpType.FREEZE -> wallet.powerUps.freeze
                        PowerUpType.ADD_TIME -> wallet.powerUps.addTime
                    }
                    InventoryCard(display = display, count = count)
                }
            }
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