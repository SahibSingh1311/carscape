package com.dmag.carscape.feature.game.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dmag.carscape.core.designsystem.component.CarScapeButton
import com.dmag.carscape.core.designsystem.theme.LuckiestGuy

@Composable
fun ConfirmLoseHeartDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "❤️ Leave this level?", fontFamily = LuckiestGuy, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "You'll lose a heart if you leave now.", fontFamily = LuckiestGuy, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(20.dp))
            Row {
                CarScapeButton(text = "Cancel", onClick = onCancel, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(12.dp))
                CarScapeButton(text = "\uD83D\uDC94 Yes", onClick = onConfirm, modifier = Modifier.weight(1f))
            }
        }
    }
}