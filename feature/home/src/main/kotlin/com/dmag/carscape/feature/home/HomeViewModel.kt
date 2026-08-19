package com.dmag.carscape.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmag.carscape.domain.repository.ProgressRepository
import com.dmag.carscape.domain.repository.WalletRepository
import com.dmag.carscape.domain.util.DailyChallenge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isDailyLocked: Boolean = false,
    val dailyCountdownText: String = "",
    val coins: Int = 0,
    val hearts: Int = 5
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            walletRepository.refreshHeartRegen()
        }

        viewModelScope.launch {
            walletRepository.wallet.collect { wallet ->
                _uiState.update {  it.copy(coins = wallet.coins, hearts = wallet.hearts)}
            }
        }

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

        _uiState.update {  it.copy(
            isDailyLocked = locked,
            dailyCountdownText = formatCountDown(secondsLeft)
        )
        }
    }

    private fun formatCountDown(totalSeconds: Long) : String {
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60
        return "%02d:%02d:%02d".format(h,m,s)
    }
}