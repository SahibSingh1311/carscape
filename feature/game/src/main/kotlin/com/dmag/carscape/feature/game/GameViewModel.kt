package com.dmag.carscape.feature.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmag.carscape.core.common.DispatcherProvider
import com.dmag.carscape.domain.model.GameState
import com.dmag.carscape.domain.repository.LevelRepository
import com.dmag.carscape.domain.repository.ProgressRepository
import com.dmag.carscape.domain.usecase.MoveVehicleUseCase
import com.dmag.carscape.domain.usecase.SlideDirection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val levelRepository: LevelRepository,
    private val progressRepository: ProgressRepository,
    private val moveVehicle: MoveVehicleUseCase,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var gameState: GameState? = null
    private var currentLevelNumber = 1

    init {
        loadLevel(1)
    }

    fun loadLevel(levelNumber: Int) {
        currentLevelNumber = levelNumber
        viewModelScope.launch(dispatchers.io) {
            _uiState.value = GameUiState.Loading
            val board = levelRepository.getLevel(levelNumber)
            val newState = GameState(board = board)
            gameState = newState
            _uiState.value = newState.toUiState(levelNumber)
        }
    }

    fun onVehicleDragged(vehicleId: String, direction: SlideDirection) {
        val current = gameState ?: return
        val updated = moveVehicle(current, vehicleId, direction)
        gameState = updated
        _uiState.value = updated.toUiState(currentLevelNumber)

        if (updated.isSolved) {
            viewModelScope.launch(dispatchers.io) {
                progressRepository.setUnlockedLevel(currentLevelNumber + 1)
            }
        }
    }

    private fun GameState.toUiState(levelNumber: Int) = GameUiState.Success(
        board = board,
        moves = moves,
        isSolved = isSolved,
        levelNumber = levelNumber
    )
}