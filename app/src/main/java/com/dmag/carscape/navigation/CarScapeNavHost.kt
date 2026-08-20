package com.dmag.carscape.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dmag.carscape.feature.game.GameScreen
import com.dmag.carscape.feature.home.HomeScreen

private object Routes {
    const val HOME = "home"
    const val GAME = "game/{mode}"
    const val MARKETPLACE = "marketplace"
    const val INVENTORY = "inventory"

    fun game(mode: String) = "game/$mode"
}

@Composable
fun CarScapeNavHost(
    navController: NavHostController = rememberNavController()
) {
    // Shared by every bottom-nav tab (Home/Marketplace/Inventory) — prevents
    // stacking duplicate destinations and keeps back-stack behavior sane.
    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(Routes.HOME) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onModeSelected = { mode ->
                    navController.navigate(Routes.game(mode.name))
                },
                onMarketplaceClick = { navigateToTab(Routes.MARKETPLACE) },
                onInventoryClick = { navigateToTab(Routes.INVENTORY) }
            )
        }
        composable(route = Routes.GAME,
            arguments = listOf(navArgument("mode") {type = NavType.StringType})) {
            // mode argument available here later if GameViewModel needs to branch on it
            GameScreen(
                onNavigateHome = { navController.popBackStack() }
            )
        }
        composable(Routes.MARKETPLACE) {
            MarketplaceScreen(
                onInventoryClick = { navigateToTab(Routes.INVENTORY) },
                onHomeClick = { navigateToTab(Routes.HOME) }
            )
        }
        composable(Routes.INVENTORY) {
            InventoryScreen(
                onMarketplaceClick = { navigateToTab(Routes.MARKETPLACE) },
                onHomeClick = { navigateToTab(Routes.HOME) }
            )
        }
    }
}