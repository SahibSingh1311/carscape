package com.dmag.carscape.feature.game.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.dmag.carscape.feature.game.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameSoundPlayer @Inject constructor(
    @ApplicationContext context: Context
) {
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var moveSoundId: Int? = null
    private var exitSoundId: Int? = null
    private var winSoundId: Int? = null

    init {
        moveSoundId = safeLoad(context, R.raw.move_sound)
        exitSoundId = safeLoad(context, R.raw.exit_sound)
        winSoundId = safeLoad(context, R.raw.win_sound)
    }

    private fun safeLoad(context: Context, resId: Int): Int? = try {
        soundPool.load(context, resId, 1)
    } catch (e: Exception) {
        Log.w("GameSoundPlayer", "Failed to load sound resource $resId", e)
        null
    }

    fun playMove() { moveSoundId?.let { soundPool.play(it, 1f, 1f, 0, 0, 1f) } }
    fun playExit() { exitSoundId?.let { soundPool.play(it, 1f, 1f, 0, 0, 1f) } }
    fun playWin() { winSoundId?.let { soundPool.play(it, 1f, 1f, 0, 0, 1f) } }

    fun release() = soundPool.release()
}