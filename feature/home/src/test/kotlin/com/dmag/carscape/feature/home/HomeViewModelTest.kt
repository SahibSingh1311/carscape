package com.dmag.carscape.feature.home

import androidx.lifecycle.viewModelScope
import com.dmag.carscape.domain.model.PowerUpInventory
import com.dmag.carscape.domain.model.Wallet
import com.dmag.carscape.domain.repository.ProgressRepository
import com.dmag.carscape.domain.repository.WalletRepository
import com.dmag.carscape.domain.util.DailyChallenge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val walletRepository: WalletRepository = mock()
    private val progressRepository: ProgressRepository = mock()

    private val walletFlow = MutableStateFlow(
        Wallet(coins = 0, hearts = 5, diamonds = 0, powerUps = PowerUpInventory(hammer = 0, freeze = 0, addTime = 0))
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        whenever(walletRepository.wallet).thenReturn(walletFlow)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // HomeViewModel's init launches an unbounded `while (true) { ...; delay(1000) }`
    // loop in viewModelScope. If it's left running, runTest's own cleanup tries to
    // drain the shared virtual-time scheduler and never finds it empty -- the test
    // hangs. Every test below creates its ViewModel through this helper and must
    // cancel viewModelScope before the runTest block returns.
    private fun createViewModel() = HomeViewModel(progressRepository, walletRepository)

    @Test
    fun `uiState reflects the current wallet once init runs`() = runTest(testDispatcher) {
        whenever(progressRepository.getLastDailyCompletionEpochDay()).thenReturn(null)
        walletFlow.value = Wallet(
            coins = 120, hearts = 3, diamonds = 7,
            powerUps = PowerUpInventory(hammer = 0, freeze = 0, addTime = 0)
        )

        val viewModel = createViewModel()
        testDispatcher.scheduler.runCurrent()

        val state = viewModel.uiState.value
        assertEquals(120, state.coins)
        assertEquals(3, state.hearts)
        assertEquals(7, state.diamonds)

        viewModel.viewModelScope.cancel()
    }

    @Test
    fun `uiState updates live as the wallet flow emits`() = runTest(testDispatcher) {
        whenever(progressRepository.getLastDailyCompletionEpochDay()).thenReturn(null)
        val viewModel = createViewModel()
        testDispatcher.scheduler.runCurrent()

        walletFlow.value = Wallet(
            coins = 50, hearts = 2, diamonds = 1,
            powerUps = PowerUpInventory(hammer = 0, freeze = 0, addTime = 0)
        )
        testDispatcher.scheduler.runCurrent()

        assertEquals(50, viewModel.uiState.value.coins)
        assertEquals(2, viewModel.uiState.value.hearts)

        viewModel.viewModelScope.cancel()
    }

    @Test
    fun `init triggers one heart-regen refresh`() = runTest(testDispatcher) {
        whenever(progressRepository.getLastDailyCompletionEpochDay()).thenReturn(null)
        val viewModel = createViewModel()
        testDispatcher.scheduler.runCurrent()

        verify(walletRepository).refreshHeartRegen()

        viewModel.viewModelScope.cancel()
    }

    @Test
    fun `daily challenge is locked when already completed today`() = runTest(testDispatcher) {
        whenever(progressRepository.getLastDailyCompletionEpochDay())
            .thenReturn(DailyChallenge.todayEpochDay())

        val viewModel = createViewModel()
        testDispatcher.scheduler.runCurrent()

        assertTrue(viewModel.uiState.value.isDailyLocked)

        viewModel.viewModelScope.cancel()
    }

    @Test
    fun `daily challenge is unlocked when the completion was on an earlier day`() =
        runTest(testDispatcher) {
            whenever(progressRepository.getLastDailyCompletionEpochDay())
                .thenReturn(DailyChallenge.todayEpochDay() - 1)

            val viewModel = createViewModel()
            testDispatcher.scheduler.runCurrent()

            assertFalse(viewModel.uiState.value.isDailyLocked)

            viewModel.viewModelScope.cancel()
        }

    @Test
    fun `daily challenge is unlocked when it was never completed`() = runTest(testDispatcher) {
        whenever(progressRepository.getLastDailyCompletionEpochDay()).thenReturn(null)

        val viewModel = createViewModel()
        testDispatcher.scheduler.runCurrent()

        assertFalse(viewModel.uiState.value.isDailyLocked)

        viewModel.viewModelScope.cancel()
    }

    @Test
    fun `daily countdown text is formatted as HH-mm-ss`() = runTest(testDispatcher) {
        whenever(progressRepository.getLastDailyCompletionEpochDay()).thenReturn(null)

        val viewModel = createViewModel()
        testDispatcher.scheduler.runCurrent()

        assertTrue(viewModel.uiState.value.dailyCountdownText.matches(Regex("""\d{2}:\d{2}:\d{2}""")))

        viewModel.viewModelScope.cancel()
    }
}