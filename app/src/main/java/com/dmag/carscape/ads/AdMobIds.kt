package com.dmag.carscape.ads

import com.dmag.carscape.BuildConfig

object AdMobIds {
    val REWARDED_AD_UNIT_ID: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/5224354917" // Google's official test ID
        } else {
            "ca-app-pub-9693169126547036/6413126374" // your real Rewarded ad unit
        }

    val APP_ID: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544~3347511713"
        } else {
            "ca-app-pub-9693169126547036~8795747136"
        }
}