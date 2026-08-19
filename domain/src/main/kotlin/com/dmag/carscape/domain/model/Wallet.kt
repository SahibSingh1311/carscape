package com.dmag.carscape.domain.model

data class PowerUpInventory(
    val hammer: Int = 0,
    val freeze: Int = 0,
    val addTime: Int = 0
)

data class Wallet(
    val coins: Int = 0,
    val hearts: Int = 5,
    val powerUps: PowerUpInventory = PowerUpInventory()
)

enum class PowerUpType { HAMMER, FREEZE, ADD_TIME}
