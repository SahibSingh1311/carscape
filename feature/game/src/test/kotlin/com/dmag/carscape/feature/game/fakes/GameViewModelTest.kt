package com.dmag.carscape.feature.game.fakes

import androidx.lifecycle.SavedStateHandle
import com.dmag.carscape.core.common.TestDispatcherProvider
import com.dmag.carscape.domain.model.GameMode
import com.dmag.carscape.domain.model.LevelDifficulty
import com.dmag.carscape.domain.model.Orientation
import com.dmag.carscape.domain.model.PowerUpInventory
import com.dmag.carscape.domain.model.Wallet
import com.dmag.carscape.domain.usecase.CheckExitUseCase
import com.dmag.carscape.domain.usecase.GetValidSlideDistanceUseCase
import com.dmag.carscape.domain.usecase.MoveVehicleUseCase
import com.dmag.carscape.domain.usecase.RemoveVehicleUseCase
import com.dmag.carscape.domain.usecase.mocks.BoardMocks
import com.dmag.carscape.domain.usecase.mocks.VehicleMocks
import com.dmag.carscape.feature.game.GameUiState
import com.dmag.carscape.feature.game.GameViewModel
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertTrue

class GameViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val moveVehicle = MoveVehicleUseCase(CheckExitUseCase())
    private val removeVehicle = RemoveVehicleUseCase()
    private val getValidSlideDistance = GetValidSlideDistanceUseCase()

    private fun createViewModel(
        mode: GameMode = GameMode.CASUAL,
        levelRepository: FakeLevelRepository = FakeLevelRepository(),
        progressRepository: FakeProgressRepository = FakeProgressRepository(),
        walletRepository: FakeWalletRepository = FakeWalletRepository(),
        interstitialAdRepository: FakeInterstitialAdRepository = FakeInterstitialAdRepository()
    ) = GameViewModel(
        savedStateHandle = SavedStateHandle(mapOf("mode" to mode.name)),
        interstitialAdRepository = interstitialAdRepository,
        levelRepository = levelRepository,
        progressRepository = progressRepository,
        walletRepository = walletRepository,
        themeCatalogRepository = FakeThemeCatalogRepository(),
        cosmeticsRepository = FakeCosmeticsRepository(),
        moveVehicle = moveVehicle,
        removeVehicle = removeVehicle,
        getValidSlideDistance = getValidSlideDistance,
        soundPlayer = FakeGameSoundPlayer(),
        dispatchers = TestDispatcherProvider(mainDispatcherRule.testDispatcher)
    )

    @Test
    fun `loads the player's unlocked level from progress on start`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val levels = FakeLevelRepository().apply {
                putLevel(GameMode.CASUAL, 1, BoardMocks.withVehicles(VehicleMocks.smallCar()))
                putLevel(GameMode.CASUAL, 5, BoardMocks.withVehicles(VehicleMocks.sedan()))
            }
            val progress = FakeProgressRepository().apply { setUnlockedLevel(GameMode.CASUAL, 5) }

            val viewModel = createViewModel(levelRepository = levels, progressRepository = progress)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is GameUiState.Success)
            assertEquals(5, (state as GameUiState.Success).levelNumber)
        }

    @Test
    fun `no level found for the requested number shows NoMoreLevels`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel(levelRepository = FakeLevelRepository())
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value is GameUiState.NoMoreLevels)
        }

    @Test
    fun `HARD level shows a difficulty warning and does not start the Timed countdown immediately`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val board = BoardMocks.board(
                vehicles = listOf(VehicleMocks.smallCar()),
                difficulty = LevelDifficulty.HARD,
                timeLimitSeconds = 30
            )
            val levels = FakeLevelRepository().apply { putLevel(GameMode.TIMED, 1, board) }
            val viewModel = createViewModel(mode = GameMode.TIMED, levelRepository = levels)
            advanceUntilIdle()

            val state = viewModel.uiState.value as GameUiState.Success
            assertTrue(state.showDifficultyWarning)

            // Timer should NOT be counting down yet — advancing time shouldn't change anything
            advanceTimeBy(5000)
            val stillState = viewModel.uiState.value as GameUiState.Success
            assertEquals(30, stillState.timeRemainingSeconds)
        }

    @Test
    fun `dismissing the difficulty warning starts the Timed countdown`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val board = BoardMocks.board(
                vehicles = listOf(VehicleMocks.smallCar()),
                difficulty = LevelDifficulty.HARD,
                timeLimitSeconds = 30
            )
            val levels = FakeLevelRepository().apply { putLevel(GameMode.TIMED, 1, board) }
            val viewModel = createViewModel(mode = GameMode.TIMED, levelRepository = levels)
            advanceUntilIdle()

            viewModel.onDifficultyWarningFinished()
            repeat(3) {
                advanceTimeBy(1_000)
                runCurrent()
            }
//            advanceUntilIdle()

            val state = viewModel.uiState.value as GameUiState.Success
            assertEquals(27, state.timeRemainingSeconds)
        }

    @Test
    fun `timer reaching zero without solving triggers TimeUp and costs a heart`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val board = BoardMocks.board(
                vehicles = listOf(VehicleMocks.smallCar()),
                timeLimitSeconds = 2
            )
            val levels = FakeLevelRepository().apply { putLevel(GameMode.TIMED, 1, board) }
            val wallet = FakeWalletRepository(Wallet(hearts = 5))
            val viewModel = createViewModel(
                mode = GameMode.TIMED,
                levelRepository = levels,
                walletRepository = wallet
            )
            advanceUntilIdle()

            advanceTimeBy(3000)
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value is GameUiState.TimeUp)
            assertEquals(4, wallet.wallet.value.hearts)
        }

    @Test
    fun `solving a Timed level awards coins and advances progress`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL)
            val exit = BoardMocks.rightExit(row = 0, cols = 6, colorIndex = 0)
            val board = BoardMocks.withVehiclesAndExits(listOf(vehicle), listOf(exit))
                .copy(coinReward = 25)
            val levels = FakeLevelRepository().apply { putLevel(GameMode.TIMED, 1, board) }
            val wallet = FakeWalletRepository()
            val progress = FakeProgressRepository()
            val viewModel = createViewModel(
                mode = GameMode.TIMED,
                levelRepository = levels,
                walletRepository = wallet,
                progressRepository = progress
            )
            advanceUntilIdle()

            viewModel.onVehicleDragged("a", 4) // reaches the exit
            advanceUntilIdle()

            assertEquals(25, wallet.wallet.value.coins)
            assertEquals(2, progress.getUnlockedLevel(GameMode.TIMED))
        }

    @Test
    fun `solving a level with a diamond reward awards diamonds`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL)
            val exit = BoardMocks.rightExit(row = 0, cols = 6, colorIndex = 0)
            val board = BoardMocks.withVehiclesAndExits(listOf(vehicle), listOf(exit))
                .copy(diamondReward = 5)
            val levels = FakeLevelRepository().apply { putLevel(GameMode.CASUAL, 1, board) }
            val wallet = FakeWalletRepository()
            val viewModel = createViewModel(
                mode = GameMode.CASUAL,
                levelRepository = levels,
                walletRepository = wallet
            )
            advanceUntilIdle()

            viewModel.onVehicleDragged("a", 4)
            advanceUntilIdle()

            assertEquals(5, wallet.wallet.value.diamonds)
        }

    @Test
    fun `exceeding the Casual boss move cap triggers MovesExceeded`() =
        runTest(mainDispatcherRule.testDispatcher) {
            // Vehicle deliberately can't reach any exit — every drag just burns a move
            val vehicle = VehicleMocks.smallCar("a", Orientation.HORIZONTAL)
            val board = BoardMocks.withVehicles(vehicle).copy(optimalMoves = 2)
            val levels = FakeLevelRepository().apply { putLevel(GameMode.CASUAL, 1, board) }
            val viewModel = createViewModel(mode = GameMode.CASUAL, levelRepository = levels)
            advanceUntilIdle()

            viewModel.onVehicleDragged("a", 1)
            viewModel.onVehicleDragged("a", -1)
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value is GameUiState.MovesExceeded)
        }

    @Test
    fun `hammer removes a vehicle and consumes one hammer power-up`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val target = VehicleMocks.smallCar("a")
            val other = VehicleMocks.sedan("b", Orientation.VERTICAL)
            val board = BoardMocks.withVehicles(target, other)
            val levels = FakeLevelRepository().apply { putLevel(GameMode.CASUAL, 1, board) }
            val wallet = FakeWalletRepository(Wallet(powerUps = PowerUpInventory(hammer = 2)))
            val viewModel = createViewModel(
                mode = GameMode.CASUAL,
                levelRepository = levels,
                walletRepository = wallet
            )
            advanceUntilIdle()

            viewModel.toggleHammerMode()
            viewModel.onVehicleTapped("a")
            advanceUntilIdle()

            val state = viewModel.uiState.value as GameUiState.Success
            assertEquals(1, state.board.vehicles.size)
            assertEquals("b", state.board.vehicles.first().id)
            assertEquals(1, wallet.wallet.value.powerUps.hammer)
        }

    @Test
    fun `hammer does nothing when the player has zero hammers`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vehicle = VehicleMocks.smallCar("a")
            val board = BoardMocks.withVehicles(vehicle)
            val levels = FakeLevelRepository().apply { putLevel(GameMode.CASUAL, 1, board) }
            val viewModel = createViewModel(
                mode = GameMode.CASUAL,
                levelRepository = levels
            ) // default 0 hammers
            advanceUntilIdle()

            viewModel.toggleHammerMode()
            viewModel.onVehicleTapped("a")
            advanceUntilIdle()

            val state = viewModel.uiState.value as GameUiState.Success
            assertEquals(1, state.board.vehicles.size) // untouched
        }

    @Test
    fun `interstitial is shown every 3rd cleared level in Casual mode`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vehicle = VehicleMocks.smallCar("a")
            val exit = BoardMocks.rightExit(row = 0, cols = 6, colorIndex = 0)
            val levels = FakeLevelRepository().apply {
                for (n in 1..4) putLevel(
                    GameMode.CASUAL,
                    n,
                    BoardMocks.withVehiclesAndExits(listOf(vehicle), listOf(exit))
                )
            }
            val ads = FakeInterstitialAdRepository()
            val viewModel = createViewModel(
                mode = GameMode.CASUAL,
                levelRepository = levels,
                interstitialAdRepository = ads
            )
            advanceUntilIdle()

            // Clear 3 levels total via onNextLevelClicked's cadence counter
            viewModel.onNextLevelClicked() // 1 cleared -> no ad
            advanceUntilIdle()
            viewModel.onNextLevelClicked() // 2 cleared -> no ad
            advanceUntilIdle()
            viewModel.onNextLevelClicked() // 3 cleared -> ad shown
            advanceUntilIdle()

            assertTrue(ads.showCallCount >= 1)
        }
}