package com.dmag.carscape.feature.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.dmag.carscape.domain.model.GameMode
import com.dmag.carscape.domain.model.Wallet
import com.dmag.carscape.domain.util.DailyChallenge
import com.dmag.carscape.feature.game.fakes.FakeProgressRepository
import com.dmag.carscape.feature.game.fakes.FakeWalletRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun heartsCoinsAndDiamondsReflectTheWallet() {
        val viewModel = HomeViewModel(
            FakeProgressRepository(),
            FakeWalletRepository(Wallet(coins = 120, hearts = 3, diamonds = 7))
        )

        composeRule.setContent {
            HomeScreen(
                onModeSelected = {},
                onMarketplaceClick = {},
                onInventoryClick = {},
                viewModel = viewModel
            )
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithText(" 3").assertIsDisplayed()
        composeRule.onNodeWithText("💎 7").assertIsDisplayed()
        composeRule.onNodeWithText("🪙 120").assertIsDisplayed()
    }

    @Test
    fun clickingAModeWithHeartsAvailableStartsItDirectly() {
        val viewModel = HomeViewModel(
            FakeProgressRepository(),
            FakeWalletRepository(Wallet(hearts = 2))
        )
        var selectedMode: GameMode? = null

        composeRule.setContent {
            HomeScreen(
                onModeSelected = { selectedMode = it },
                onMarketplaceClick = {},
                onInventoryClick = {},
                viewModel = viewModel
            )
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("TIMED MODE").performClick()

        assertEquals(GameMode.TIMED, selectedMode)
    }

    @Test
    fun clickingAModeWithNoHeartsShowsTheNoHeartsDialogInstead() {
        val viewModel = HomeViewModel(
            FakeProgressRepository(),
            FakeWalletRepository(Wallet(hearts = 0))
        )
        var selectedMode: GameMode? = null
        var dialogShown = false

        composeRule.setContent {
            HomeScreen(
                onModeSelected = { selectedMode = it },
                onMarketplaceClick = {},
                onInventoryClick = {},
                noHeartsDialog = { _, _ -> dialogShown = true },
                viewModel = viewModel
            )
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("CASUAL MODE").performClick()
        composeRule.waitForIdle()

        assertTrue(dialogShown)
        assertNull(selectedMode)
    }

    @Test
    fun earningAHeartFromTheDialogStartsThePendingMode() {
        val viewModel = HomeViewModel(
            FakeProgressRepository(),
            FakeWalletRepository(Wallet(hearts = 0))
        )
        var selectedMode: GameMode? = null

        composeRule.setContent {
            HomeScreen(
                onModeSelected = { selectedMode = it },
                onMarketplaceClick = {},
                onInventoryClick = {},
                noHeartsDialog = { _, onHeartEarned -> onHeartEarned() },
                viewModel = viewModel
            )
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("TIMED MODE").performClick()
        composeRule.waitForIdle()

        assertEquals(GameMode.TIMED, selectedMode)
    }

    @Test
    fun dailyButtonShowsLockedStateWhenAlreadyCompletedToday() {
        val progressRepo = FakeProgressRepository().apply {
            // Seed through the real suspend API rather than a constructor param,
            // since this fake has no param for it.
        }
        // FakeProgressRepository has no constructor hook for an initial
        // lastDailyCompletionEpochDay, so set it through the real suspend call
        // before composing -- kotlinx.coroutines.test isn't needed here since
        // this runs synchronously on a plain (non-suspend) test thread via runBlocking.
        runBlocking {
            progressRepo.setLastDailyCompletionEpochDay(DailyChallenge.todayEpochDay())
        }

        val viewModel = HomeViewModel(progressRepo, FakeWalletRepository())

        composeRule.setContent {
            HomeScreen(
                onModeSelected = {},
                onMarketplaceClick = {},
                onInventoryClick = {},
                viewModel = viewModel
            )
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("DAILY").assertIsDisplayed()
    }

    @Test
    fun dailyButtonShowsUnlockedStateWhenNotCompletedToday() {
        val viewModel = HomeViewModel(FakeProgressRepository(), FakeWalletRepository())

        composeRule.setContent {
            HomeScreen(
                onModeSelected = {},
                onMarketplaceClick = {},
                onInventoryClick = {},
                viewModel = viewModel
            )
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("DAILY CHALLENGE").assertIsDisplayed()
    }
}