package com.dmag.carscape.ads

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dmag.carscape.R
import kotlinx.coroutines.delay

@Composable
fun RewardedAdBubble(
    viewModel: RewardedAdViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.message) {
        if (state.message != null) {
            delay(1500)
            viewModel.dismissMessage()
        }
    }

    Box(
        modifier = modifier
            .padding(4.dp)
            .width(100.dp) // adjust to match your image proportions
            .clip(CircleShape)
            .clickable(enabled = !state.isLoading) { viewModel.onBubbleClicked() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.coins),
            contentDescription = "Watch ad to earn coins",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.size(140.dp) // tweak width/height as needed
        )

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = Color.White,
                strokeWidth = 3.dp
            )
        }
    }
}