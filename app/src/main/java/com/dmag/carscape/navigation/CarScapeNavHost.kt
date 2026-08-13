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
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onModeSelected = { mode ->
                    navController.navigate(Routes.game(mode.name))
                },
                onMarketplaceClick = { navController.navigate(Routes.MARKETPLACE) },
                onInventoryClick = { navController.navigate(Routes.INVENTORY) }
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
            MarketplaceScreen()
        }
        composable(Routes.INVENTORY) {
            InventoryScreen()
        }
    }
}