package com.dmag.carscape.core.designsystem.theme

import com.dmag.carscape.core.designsystem.R

// Local art resolution, keyed by the Firestore-driven theme id — mirrors
// VehicleBlock's drawableForLength pattern. Add a new branch here + a new
// drawable whenever a new board theme's art is ready; no Firestore change needed.
fun boardBackgroundFor(themeId: String): Int? = when (themeId) {
    "default" -> R.drawable.board_background
    // "cyberpunk" -> R.drawable.bg_board_cyberpunk   (add once art exists)
    // "space" -> R.drawable.bg_board_space
    // "water" -> R.drawable.bg_board_water
    // "train" -> R.drawable.bg_board_train
    else -> null
}