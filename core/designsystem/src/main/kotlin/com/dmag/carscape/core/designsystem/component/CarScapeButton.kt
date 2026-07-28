package com.dmag.carscape.core.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmag.carscape.core.designsystem.theme.ExitGlow

@Composable
fun CarScapeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ExitGlow)
    ) {
        Text(text)
    }
}