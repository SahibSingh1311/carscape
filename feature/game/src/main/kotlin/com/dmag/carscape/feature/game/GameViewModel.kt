package com.dmag.carscape.feature.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmag.carscape.core.common.DispatcherProvider
import com.dmag.carscape.domain.model.GameMode
import com.dmag.carscape.domain.model.GameState
import com.dmag.carscape.domain.model.Orientation
import com.dmag.carscape.domain.model.Vehicle
import com.dmag.carscape.domain.repository.LevelRepository
import com.dmag.carscape.domain.repository.ProgressRepository
import com.dmag.carscape.domain.repository.WalletRepository
import com.dmag.carscape.domain.usecase.GetValidSlideDistanceUseCase
import com.dmag.carscape.domain.usecase.MoveVehicleUseCase
import com.dmag.carscape.domain.usecase.SlideDirection
import com.dmag.carscape.domain.util.DailyChallenge
import com.dmag.carscape.feature.game.audio.GameSoundPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val levelRepository: LevelRepository,
    private val progressRepository: ProgressRepository,
    private val walletRepository: WalletRepository,
    private val moveVehicle: MoveVehicleUseCase,
    private val getValidSlideDistance: GetValidSlideDistanceUseCase,
    private val soundPlayer: GameSoundPlayer,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val mode: GameMode = savedStateHandle.get<String>("mode")
        ?.let { runCatching { GameMode.valueOf(it) }.getOrNull() }
        ?: GameMode.CASUAL

    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var gameState: GameState? = null
    private var currentLevelNumber = 1
    private var timerJob: Job? = null
    private var timeRemaining = 0

    init {
        viewModelScope.launch(dispatchers.io) {
            if (mode == GameMode.DAILY) {
                checkDailyLockAndLoad()
            } else {
                val startLevel = progressRepository.getUnlockedLevel(mode)
                loadLevel(startLevel)
            }
        }
    }

    private suspend fun checkDailyLockAndLoad() {
        val lastCompleted = progressRepository.getLastDailyCompletionEpochDay()
        val today = DailyChallenge.todayEpochDay()
        if (lastCompleted == today) {
            _uiState.value = GameUiState.DailyLocked(DailyChallenge.secondsUntilNextDay())
        } else {
            loadLevel(DailyChallenge.currentLevelNumber())
        }
    }

    fun loadLevel(levelNumber: Int) {
        currentLevelNumber = levelNumber
        timerJob?.cancel()
        viewModelScope.launch(dispatchers.io) {
            _uiState.value = GameUiState.Loading
            try {
                val board = levelRepository.getLevel(mode, levelNumber)
                val newState = GameState(board = board)
                gameState = newState
                timeRemaining = board.timeLimitSeconds
                _uiState.value = newState.toUiState(levelNumber, timeRemaining)

                if (mode == GameMode.TIMED) {
                    startTimer()
                }
            } catch (e: NoSuchElementException) {
                _uiState.value = GameUiState.NoMoreLevels(lastLevelNumber = levelNumber - 1)
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (timeRemaining > 0) {
                delay(1000)
                timeRemaining -= 1
                val current = gameState ?: break
                _uiState.value = current.toUiState(currentLevelNumber, timeRemaining)
            }
            if(timeRemaining <= 0) {
                val current = gameState
                if (current != null && !current.isSolved) {
                    _uiState.value = GameUiState.TimeUp(levelNumber = currentLevelNumber)
                }
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
    }

    fun resumeTimer(){
        if (mode == GameMode.TIMED && timeRemaining > 0) {
            startTimer()
        }
    }

    fun getDragBounds(vehicle: Vehicle): DragBounds {
        val board = gameState?.board ?: return DragBounds(0, 0)
        val forwardDirection = if (vehicle.orientation == Orientation.HORIZONTAL) SlideDirection.RIGHT else SlideDirection.DOWN
        val backwardDirection = if (vehicle.orientation == Orientation.HORIZONTAL) SlideDirection.LEFT else SlideDirection.UP

        val forward = getValidSlideDistance(board, vehicle, forwardDirection)
        val backward = getValidSlideDistance(board, vehicle, backwardDirection)
        return DragBounds(maxForwardCells = forward, maxBackwardCells = kotlin.math.abs(backward))
    }

    private fun GameState.toUiState(levelNumber: Int, timeRemainingSeconds : Int) = GameUiState.Success(
        board = board,
        moves = moves,
        isSolved = isSolved,
        levelNumber = levelNumber,
        mode = mode,
        timeRemainingSeconds = if(mode == GameMode.TIMED) timeRemainingSeconds else null
    )

    fun onVehicleDragged(vehicleId: String, distance: Int) {
        val current = gameState ?: return
        val previousVehicleCount = current.board.vehicles.size

        val updated = moveVehicle(current, vehicleId, distance)
        if (updated === current) return // no-op move, nothing to update or play

        gameState = updated
        _uiState.value = updated.toUiState(currentLevelNumber, timeRemaining)

        if (updated.board.vehicles.size < previousVehicleCount) {
            soundPlayer.playExit()
        } else {
            soundPlayer.playMove()
        }

        if (updated.isSolved) {
            timerJob?.cancel()
            soundPlayer.playWin()
            viewModelScope.launch(dispatchers.io) {
                if (mode == GameMode.DAILY) {
                    progressRepository.setLastDailyCompletionEpochDay(DailyChallenge.todayEpochDay())
                } else {
                    progressRepository.setUnlockedLevel(mode, currentLevelNumber + 1)
                }
                if (mode == GameMode.TIMED || mode == GameMode.DAILY) {
                    walletRepository.addCoins(updated.board.coinReward)
                }
            }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        soundPlayer.release()
        super.onCleared()
    }
}

data class DragBounds(val maxForwardCells: Int, val maxBackwardCells: Int)