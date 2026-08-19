package com.dmag.carscape.domain.repository

import com.dmag.carscape.domain.model.PowerUpType
import com.dmag.carscape.domain.model.Wallet
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    val wallet: Flow<Wallet>

    suspend fun addCoins(amount: Int)
    suspend fun spendCoins(amount: Int): Boolean

    suspend fun loseHeart()
    suspend fun addHeart()
    suspend fun refreshHeartRegen()


    suspend fun addPowerUp(type: PowerUpType, count: Int = 1)
    suspend fun consumePowerUp(type: PowerUpType): Boolean

    companion object {
        const val MAX_HEARTS = 5
        const val HEART_REGEN_SECONDS = 15 * 60L
    }
}