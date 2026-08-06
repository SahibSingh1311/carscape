package com.dmag.carscape.feature.home

enum class GameMode(val displayName: String, val description: String) {
    DAILY("Daily Challenge", "One new puzzle every day"),
    TIMED("Timed Mode", "Race the clock, earn coins"),
    CASUAL("Casual Mode", "No timer, no pressure")
}