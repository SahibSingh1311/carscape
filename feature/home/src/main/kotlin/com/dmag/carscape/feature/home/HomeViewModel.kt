package com.dmag.carscape.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmag.carscape.domain.repository.ProgressRepository
import com.dmag.carscape.domain.util.DailyChallenge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isDailyLocked: Boolean = false,
    val dailyCountdownText: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val progressRepository: ProgressRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                refreshDailyStatus()
                delay(1000)
            }
        }
    }

    private suspend fun refreshDailyStatus() {
        val lastComleted = progressRepository.getLastDailyCompletionEpochDay()
        val today = DailyChallenge.todayEpochDay()
        val locked = lastComleted == today
        val secondsLeft = DailyChallenge.secondsUntilNextDay()

        _uiState.value = HomeUiState(
            isDailyLocked = locked,
            dailyCountdownText = formatCountDown(secondsLeft)
        )
    }

    private fun formatCountDown(totalSeconds: Long) : String {
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60
        return "%02d:%02d:%02d".format(h,m,s)
    }
}