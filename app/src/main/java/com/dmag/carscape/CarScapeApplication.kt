package com.dmag.carscape

import android.app.Application
import com.dmag.carscape.domain.repository.WalletRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class CarScapeApplication : Application() {
    @Inject lateinit var walletRepository: WalletRepository

    private val appScope = CoroutineScope(SupervisorJob())
    override fun onCreate() {
        super.onCreate()
//        if (BuildConfig.DEBUG) {
//            FirebaseFirestore.setLoggingEnabled(true)
//        }
        appScope.launch {
            walletRepository.refreshHeartRegen()
        }
    }
}