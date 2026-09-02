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
    @ApplicationContext private val context: Context
): RewardedAdRepository {
    private var rewardedAd: RewardedAd? = null
    private var currentActivity: Activity? = null

    fun setCurrentActivity(activity: Activity?) {
        currentActivity = activity
    }

    override suspend fun isAdReady(): Boolean = rewardedAd != null

    override suspend fun loadAd() {
        if (rewardedAd != null) return
        android.util.Log.d("AdRepo", "loadAd() starting...")
        suspendCancellableCoroutine <Unit> { continuation ->
            RewardedAd.load(
                context,
                AdMobIds.REWARDED_AD_UNIT_ID,
                AdRequest.Builder().build(),
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {

                        android.util.Log.d("AdRepo", "Ad loaded successfully")
                        rewardedAd = ad
                        if (continuation.isActive) continuation.resume(Unit, onCancellation = null)
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        android.util.Log.e("AdRepo", "Ad failed to load: ${error.message}, code=${error.code}")

                        rewardedAd = null
                        if (continuation.isActive) continuation.resume(Unit, onCancellation = null)
                    }
                }
            )
        }
    }

    override suspend fun showAd(): Boolean {
        val ad = rewardedAd ?: return false
        android.util.Log.d("AdRepo", "showAd() called, rewardedAd is null? ${ad == null}")
        val activity = currentActivity ?: return false
        android.util.Log.d("AdRepo", "activity found? ${activity != null}")


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