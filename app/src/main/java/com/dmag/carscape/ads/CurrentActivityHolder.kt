package com.dmag.carscape.ads

import android.app.Activity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentActivityHolder @Inject constructor() {
    var activity: Activity? = null
}