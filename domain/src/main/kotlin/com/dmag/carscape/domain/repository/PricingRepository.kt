package com.dmag.carscape.domain.repository

import com.dmag.carscape.domain.model.PowerUpType

interface PricingRepository {
    suspend fun getPrice(type: PowerUpType): Int
}