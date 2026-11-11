package com.dmag.carscape.data.repository

import com.dmag.carscape.core.common.DispatcherProvider
import com.dmag.carscape.domain.model.PowerUpType
import com.dmag.carscape.domain.repository.PricingRepository
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PricingRepositoryImpl @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    private val dispatchers: DispatcherProvider
) : PricingRepository {

    override suspend fun getPrice(type: PowerUpType): Int = withContext(dispatchers.io) {
        remoteConfig.fetchAndActivate().await()
        val key = when (type) {
            PowerUpType.HAMMER -> "price_hammer"
            PowerUpType.FREEZE -> "price_freeze"
            PowerUpType.ADD_TIME -> "price_add_time"
        }
        remoteConfig.getLong(key).toInt()
    }
}