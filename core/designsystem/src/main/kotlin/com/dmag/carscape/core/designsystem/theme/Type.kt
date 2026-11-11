package com.dmag.carscape.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val CarScapeTypography = Typography(
    headlineLarge = TextStyle(fontFamily = LuckiestGuy, fontWeight = FontWeight.Normal, fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = LuckiestGuy, fontWeight = FontWeight.Normal, fontSize = 24.sp),
    titleMedium = TextStyle(fontFamily = LuckiestGuy, fontWeight = FontWeight.Normal, fontSize = 18.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp), // regular text stays on the default system font
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp)
)