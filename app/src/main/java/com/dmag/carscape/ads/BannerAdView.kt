package com.dmag.carscape.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAdView(modifier: Modifier = Modifier) {
    val adViewRef = remember { arrayOfNulls<AdView>(1) }

    DisposableEffect(Unit) {
        onDispose {
            adViewRef[0]?.destroy() // prevents a leaked ad session when the game screen is left
        }
    }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = AdMobIds.BANNER_AD_UNIT_ID
                loadAd(AdRequest.Builder().build())
                adViewRef[0] = this
            }
        }
    )
}