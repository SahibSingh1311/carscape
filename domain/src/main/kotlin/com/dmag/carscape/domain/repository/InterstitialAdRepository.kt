package com.dmag.carscape.domain.repository

interface InterstitialAdRepository {
    suspend fun isAdReady(): Boolean
    suspend fun loadAd()
    /** Suspends until the ad is dismissed (watched or skipped). Returns false if no ad was shown. */
    suspend fun showAd(): Boolean
}