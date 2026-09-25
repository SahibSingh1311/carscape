package com.dmag.carscape.feature.game.fakes

import com.dmag.carscape.domain.model.PowerUpInventory
import com.dmag.carscape.domain.model.PowerUpType
import com.dmag.carscape.domain.model.Wallet
import com.dmag.carscape.domain.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class FakeWalletRepository(initial: Wallet = Wallet()) : WalletRepository {
    private val _wallet = MutableStateFlow(initial)
    override val wallet: StateFlow<Wallet> get() = _wallet

    override suspend fun addCoins(amount: Int) {
        _wallet.update { it.copy(coins = it.coins + amount) }
    }

    override suspend fun spendCoins(amount: Int): Boolean {
        if (_wallet.value.coins < amount) return false
        _wallet.update { it.copy(coins = it.coins - amount) }
        return true
    }

    override suspend fun addDiamonds(amount: Int) {
        _wallet.update { it.copy(diamonds = it.diamonds + amount) }
    }

    override suspend fun spendDiamonds(amount: Int): Boolean {
        if (_wallet.value.diamonds < amount) return false
        _wallet.update { it.copy(diamonds = it.diamonds - amount) }
        return true
    }

    override suspend fun loseHeart() {
        _wallet.update { it.copy(hearts = (it.hearts - 1).coerceAtLeast(0)) }
    }

    override suspend fun addHeart() {
        _wallet.update { it.copy(hearts = (it.hearts + 1).coerceAtMost(WalletRepository.MAX_HEARTS)) }
    }

    override suspend fun refreshHeartRegen() { /* no-op for tests */ }

    override suspend fun addPowerUp(type: PowerUpType, count: Int) {
        _wallet.update { it.copy(powerUps = it.powerUps.withDelta(type, count)) }
    }

    override suspend fun consumePowerUp(type: PowerUpType): Boolean {
        val current = _wallet.value.powerUps.countFor(type)
        if (current <= 0) return false
        _wallet.update { it.copy(powerUps = it.powerUps.withDelta(type, -1)) }
        return true
    }

    private fun PowerUpInventory.countFor(type: PowerUpType) = when (type) {
        PowerUpType.HAMMER -> hammer
        PowerUpType.FREEZE -> freeze
        PowerUpType.ADD_TIME -> addTime
    }

    private fun PowerUpInventory.withDelta(type: PowerUpType, delta: Int) = when (type) {
        PowerUpType.HAMMER -> copy(hammer = hammer + delta)
        PowerUpType.FREEZE -> copy(freeze = freeze + delta)
        PowerUpType.ADD_TIME -> copy(addTime = addTime + delta)
    }
}