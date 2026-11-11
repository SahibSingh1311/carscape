package com.dmag.carscape.feature.game.fakes

import com.dmag.carscape.feature.game.audio.GameSoundPlayer

class FakeGameSoundPlayer : GameSoundPlayer {
    override fun playMove() {}
    override fun playExit() {}
    override fun playWin() {}
    override fun playSiren() {}
    override fun release() {}
}