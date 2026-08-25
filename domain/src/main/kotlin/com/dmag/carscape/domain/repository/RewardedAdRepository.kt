package com.dmag.carscape.domain.repository

interface RewardedAdRepository {
    suspend fun isAdReady(): Boolean
    suspend fun loadAd()
    suspend fun showAd(): Boolean
}