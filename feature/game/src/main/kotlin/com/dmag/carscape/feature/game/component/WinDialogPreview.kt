package com.dmag.carscape.feature.game.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true)
@Composable
fun WinDialogPreview() {
    WinDialog(moves = 10,
        onNextLevel = {},
        onRetry = {})
}