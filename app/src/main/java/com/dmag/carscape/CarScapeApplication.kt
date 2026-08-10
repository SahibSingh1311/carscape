package com.dmag.carscape

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CarScapeApplication : Application()
//{
//    override fun onCreate() {
//        super.onCreate()
//        if (BuildConfig.DEBUG) {
//            FirebaseFirestore.setLoggingEnabled(true)
//        }
//    }
//}