package dk.kea.androidgame.martin.myfirstgameengine.engine.sound

import android.media.SoundPool

class Sound(private val soundPool: SoundPool, private val soundId: Int) {

    fun play(volume: Float) {
        soundPool.play(soundId, volume, volume, 0, 0, 1f)
    }

    fun dispose() {
        soundPool.unload(soundId)
    }
}
