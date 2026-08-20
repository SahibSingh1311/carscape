package com.dmag.carscape.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmag.carscape.domain.model.PowerUpType
import com.dmag.carscape.domain.model.Wallet
import com.dmag.carscape.domain.repository.PricingRepository
import com.dmag.carscape.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MarketplaceItem(
    val type: PowerUpType,
    val label: String,
    val emoji: String,
    val price: Int
)

data class MarketplaceUiState(
    val wallet: Wallet = Wallet(),
    val items: List<MarketplaceItem> = emptyList(),
    val purchaseMessage: String? = null,
    val isLoading: Boolean = true
    )

@HiltViewModel
class MarketplaceViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val pricingRepository: PricingRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(MarketplaceUiState())
    val uiState: StateFlow<MarketplaceUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            walletRepository.wallet.collect { wallet ->
                _uiState.update { it.copy(wallet = wallet) }
            }
        }

        viewModelScope.launch {
            val items = listOf(
                MarketplaceItem(PowerUpType.HAMMER, "Hammer", "🔨", pricingRepository.getPrice(PowerUpType.HAMMER)),
                MarketplaceItem(PowerUpType.FREEZE, "Freeze", "❄️", pricingRepository.getPrice(PowerUpType.FREEZE)),
                MarketplaceItem(PowerUpType.ADD_TIME, "Add Time", "⏱️", pricingRepository.getPrice(PowerUpType.ADD_TIME))
            )
            _uiState.update { it.copy(items = items, isLoading = false) }
        }
    }

    fun buy(item: MarketplaceItem) {
        viewModelScope.launch {
            val success = walletRepository.spendCoins(item.price)
            if (success) {
                walletRepository.addPowerUp(item.type, 1)
                _uiState.update { it.copy(purchaseMessage = "Bought ${item.label}!") }
            } else {
                _uiState.update { it.copy(purchaseMessage = "Not enough coins") }
            }
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(purchaseMessage = null) }
    }
}