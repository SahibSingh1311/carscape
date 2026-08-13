package com.dmag.carscape.feature.game.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmag.carscape.core.designsystem.component.CarScapeButton

@Composable
fun TimeUpDialog (
    onRetry: () -> Unit,
    onHome: () -> Unit
) {
    Dialog(onDismissRequest = { /* force a choice */ }) {
        Column(modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Time's Up", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            CarScapeButton(text = "Retry", onClick = onRetry)
            Spacer(modifier = Modifier.height(20.dp))
            CarScapeButton(text = "Home", onClick = onHome)
        }
    }
}