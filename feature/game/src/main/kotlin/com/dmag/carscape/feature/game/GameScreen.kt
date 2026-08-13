package com.dmag.carscape.feature.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dmag.carscape.core.designsystem.component.CarScapeButton
import com.dmag.carscape.domain.model.GameMode
import com.dmag.carscape.feature.game.component.BoardCanvas
import com.dmag.carscape.feature.game.component.PauseDialog
import com.dmag.carscape.feature.game.component.TimeUpDialog
import com.dmag.carscape.feature.game.component.VehicleBlock
import com.dmag.carscape.feature.game.component.WinDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    onNavigateHome: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var isPaused by remember { mutableStateOf(false) }

    LaunchedEffect(isPaused) {
        if(isPaused) viewModel.pauseTimer() else viewModel.resumeTimer()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { isPaused = true }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Pause")
                    }
                },
                title = {
                    val current = state
                    if (current is GameUiState.Success) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                        ) {
                            Text("Level ${current.levelNumber}")
                            if (current.mode == GameMode.TIMED && current.timeRemainingSeconds != null) {
                                Text("⏱ ${current.timeRemainingSeconds}s")
                            }
                            Text("Moves: ${current.moves}")
                        }
                    } else {
                        Text("CarScape")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (val current = state) {
                is GameUiState.Loading -> CircularProgressIndicator()

                is GameUiState.TimeUp -> {
                    TimeUpDialog(
                        onRetry = { viewModel.loadLevel(current.levelNumber) },
                        onHome = onNavigateHome
                    )
                }

                is GameUiState.DailyLocked -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("You've completed today's challenge!")
                        Text("Come back tomorrow")
                        Spacer(modifier = Modifier.padding(8.dp))
                        CarScapeButton(
                            text = "Back to Home",
                            onClick = onNavigateHome
                        )
                    }
                }

                is GameUiState.NoMoreLevels -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("You've cleared all available levels!")
                        Text("More levels coming soon 🚗")
                        Spacer(modifier = Modifier.height(16.dp))
                        com.dmag.carscape.core.designsystem.component.CarScapeButton(
                            text = "Back to Home",
                            onClick = onNavigateHome
                        )
                    }
                }

                is GameUiState.Success -> {
                    Box(modifier = Modifier.padding(16.dp)) {
                        BoardCanvas(board = current.board) { cellSizePx ->
                            val cellSizeDp = with(LocalDensity.current) { cellSizePx.toDp() }
                            current.board.vehicles.forEach { vehicle ->
                                val bounds = viewModel.getDragBounds(vehicle)
                                VehicleBlock(
                                    vehicle = vehicle,
                                    cellSizeDp = cellSizeDp,
                                    cellSizePx = cellSizePx,
                                    maxForwardCells = bounds.maxForwardCells,
                                    maxBackwardCells = bounds.maxBackwardCells,
                                    onDragCommitted = { cellsMoved ->
                                        viewModel.onVehicleDragged(vehicle.id, cellsMoved)
                                    }
                                )
                            }
                        }
                        if (current.isSolved) {
                            if (current.mode == GameMode.DAILY) {
                                WinDialog(
                                    moves = current.moves,
                                    onNextLevel = null,  // no next level for Daily — locked until tomorrow
                                    onRetry = onNavigateHome  // repurpose as the single available action: back to Home
                                )
                            } else {
                                WinDialog(
                                    moves = current.moves,
                                    onNextLevel = { viewModel.loadLevel(current.levelNumber + 1) },
                                    onRetry = { viewModel.loadLevel(current.levelNumber) }
                                )
                            }
                        }

                        if (isPaused) {
                            PauseDialog(
                                onResume = { isPaused = false },
                                onRestart = {
                                    isPaused = false
                                    viewModel.loadLevel(current.levelNumber)
                                },
                                onHome = onNavigateHome
                            )
                        }
                    }
                }
            }
        }
    }
}