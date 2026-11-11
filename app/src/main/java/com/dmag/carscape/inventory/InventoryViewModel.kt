package com.dmag.carscape.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmag.carscape.domain.model.Wallet
import com.dmag.carscape.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    walletRepository: WalletRepository
) : ViewModel() {

    private val _wallet = MutableStateFlow(Wallet())
    val wallet: StateFlow<Wallet> = _wallet.asStateFlow()

    init {
        viewModelScope.launch {
            walletRepository.wallet.collect { w ->
                _wallet.update { w }
            }
        }
    }
}