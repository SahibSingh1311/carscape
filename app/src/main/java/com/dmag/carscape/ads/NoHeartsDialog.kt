package com.dmag.carscape.ads

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.dmag.carscape.core.designsystem.component.CarScapeButton
import com.dmag.carscape.core.designsystem.theme.LuckiestGuy

@Composable
fun NoHeartsDialog(
    onDismiss: () -> Unit,
    onHeartEarned: () -> Unit,
    viewModel: HeartRewardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Out of Hearts!", fontFamily = LuckiestGuy, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Watch an ad to get 1 more ❤️ and keep playing.", fontFamily = LuckiestGuy, color = Color.White, textAlign = TextAlign.Center )
            Spacer(modifier = Modifier.height(20.dp))
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                CarScapeButton(text = "Watch Ad", onClick = { viewModel.watchAd(onHeartEarned) })
                Spacer(modifier = Modifier.height(8.dp))
                CarScapeButton(text = "Cancel", onClick = onDismiss)
            }
        }
    }
}