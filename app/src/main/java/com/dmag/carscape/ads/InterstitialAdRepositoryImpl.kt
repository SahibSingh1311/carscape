package com.dmag.carscape.ads

import android.content.Context
import com.dmag.carscape.domain.repository.InterstitialAdRepository
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterstitialAdRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val currentActivityHolder: CurrentActivityHolder
) : InterstitialAdRepository {

    private var interstitialAd: InterstitialAd? = null

    override suspend fun isAdReady(): Boolean = interstitialAd != null

    override suspend fun loadAd() {
        if (interstitialAd != null) return
        suspendCancellableCoroutine<Unit> { continuation ->
            InterstitialAd.load(
                context,
                AdMobIds.INTERSTITIAL_AD_UNIT_ID,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        if (continuation.isActive) continuation.resume(Unit, onCancellation = null)
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        interstitialAd = null
                        if (continuation.isActive) continuation.resume(Unit, onCancellation = null)
                    }
                }
            )
        }
    }

    override suspend fun showAd(): Boolean {
        val ad = interstitialAd ?: return false
        val activity = currentActivityHolder.activity ?: return false

        return suspendCancellableCoroutine { continuation ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    if (continuation.isActive) continuation.resume(true, onCancellation = null)
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    if (continuation.isActive) continuation.resume(false, onCancellation = null)
                }
            }
            ad.show(activity)
        }
    }
}