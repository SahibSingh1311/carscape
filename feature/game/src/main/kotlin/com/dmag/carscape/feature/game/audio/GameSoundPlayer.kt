package com.dmag.carscape.feature.game.audio

interface GameSoundPlayer {
    fun playMove()
    fun playExit()
    fun playWin()
    fun playSiren()
    fun release()
}