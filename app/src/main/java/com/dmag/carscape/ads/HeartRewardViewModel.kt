package com.dmag.carscape.ads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmag.carscape.domain.repository.RewardedAdRepository
import com.dmag.carscape.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HeartRewardUiState(val isLoading: Boolean = false)

@HiltViewModel
class HeartRewardViewModel @Inject constructor(
    private val rewardedAdRepository: RewardedAdRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HeartRewardUiState())
    val uiState: StateFlow<HeartRewardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { rewardedAdRepository.loadAd() }
    }

    fun watchAd(onHeartEarned: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            if (!rewardedAdRepository.isAdReady()) {
                rewardedAdRepository.loadAd()
            }
            val earned = rewardedAdRepository.showAd()
            if (earned) {
                walletRepository.addHeart()
                onHeartEarned()
            }
            _uiState.update { it.copy(isLoading = false) }
            viewModelScope.launch { rewardedAdRepository.loadAd() } // preload the next one
        }
    }
}