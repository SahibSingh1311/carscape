package com.dmag.carscape

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dmag.carscape.ads.CurrentActivityHolder
import com.dmag.carscape.core.designsystem.theme.CarScapeTheme
import com.dmag.carscape.navigation.CarScapeNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var currentActivityHolder: CurrentActivityHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarScapeTheme {
                CarScapeNavHost()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        currentActivityHolder.activity = this
    }

    override fun onPause() {
        currentActivityHolder.activity = null
        super.onPause()
    }
}