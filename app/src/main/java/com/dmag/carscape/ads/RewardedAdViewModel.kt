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

private const val AD_REWARD_COINS = 10

data class RewardedAdUiState(
    val isLoading: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class RewardedAdViewModel @Inject constructor(
    private val rewardedAdRepository: RewardedAdRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RewardedAdUiState())
    val uiState: StateFlow<RewardedAdUiState> = _uiState.asStateFlow()

    init {
        preloadAd()
    }

    private fun preloadAd() {
        viewModelScope.launch {
            rewardedAdRepository.loadAd()
        }
    }

    fun onBubbleClicked() {
        android.util.Log.d("AdVM", "Bubble clicked")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }

            if (!rewardedAdRepository.isAdReady()) {
                android.util.Log.d("AdVM", "Ad not ready, loading...")
                rewardedAdRepository.loadAd()
            }

            val earned = rewardedAdRepository.showAd()
            android.util.Log.d("AdVM", "showAd() returned: $earned")
            if (earned) {
                walletRepository.addCoins(AD_REWARD_COINS)
                _uiState.update { it.copy(isLoading = false, message = "+$AD_REWARD_COINS coins!") }
            } else {
                _uiState.update { it.copy(isLoading = false, message = null) }
            }

            preloadAd() // get the next one ready in the background
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(message = null) }
    }
}