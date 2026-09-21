package com.dmag.carscape.ads

import android.app.Activity
import android.content.Context
import com.dmag.carscape.domain.repository.RewardedAdRepository
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardedAdRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val currentActivityHolder: CurrentActivityHolder
): RewardedAdRepository {
    private var rewardedAd: RewardedAd? = null

    override suspend fun isAdReady(): Boolean = rewardedAd != null

    override suspend fun loadAd() {
        if (rewardedAd != null) return
        suspendCancellableCoroutine <Unit> { continuation ->
            RewardedAd.load(
                context,
                AdMobIds.REWARDED_AD_UNIT_ID,
                AdRequest.Builder().build(),
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {

                        rewardedAd = ad
                        if (continuation.isActive) continuation.resume(Unit, onCancellation = null)
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {

                        rewardedAd = null
                        if (continuation.isActive) continuation.resume(Unit, onCancellation = null)
                    }
                }
            )
        }
    }

    override suspend fun showAd(): Boolean {
        val ad = rewardedAd ?: return false
        val activity = currentActivityHolder.activity ?: return false

        return suspendCancellableCoroutine { continuation ->
            var earnedReward = false

            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    if (continuation.isActive) continuation.resume(earnedReward, onCancellation = null)
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    rewardedAd = null
                    if (continuation.isActive) continuation.resume(false, onCancellation = null)
                }
            }

            ad.show(activity) { earnedReward = true }
        }
    }

    private fun Context.findActivity(): Activity? {
        var ctx = this
        while (ctx is android.content.ContextWrapper) {
            if (ctx is Activity) return ctx
            ctx = ctx.baseContext
        }
        return null
    }
}