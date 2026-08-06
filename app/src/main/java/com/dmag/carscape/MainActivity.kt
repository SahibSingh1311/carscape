package com.dmag.carscape

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dmag.carscape.core.designsystem.theme.CarScapeTheme
import com.dmag.carscape.navigation.CarScapeNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarScapeTheme {
                CarScapeNavHost()
            }
        }
    }
}