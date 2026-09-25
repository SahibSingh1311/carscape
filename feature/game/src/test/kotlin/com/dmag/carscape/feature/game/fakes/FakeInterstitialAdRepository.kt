package com.dmag.carscape.feature.game.fakes

import com.dmag.carscape.domain.repository.InterstitialAdRepository

class FakeInterstitialAdRepository : InterstitialAdRepository {
    var loadCallCount = 0
        private set
    var showCallCount = 0
        private set
    var adReady = false

    override suspend fun isAdReady(): Boolean = adReady
    override suspend fun loadAd() {
        loadCallCount++
        adReady = true
    }
    override suspend fun showAd(): Boolean {
        showCallCount++
        adReady = false
        return true
    }
}