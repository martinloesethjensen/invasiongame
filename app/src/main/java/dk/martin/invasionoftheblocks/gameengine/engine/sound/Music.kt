package dk.kea.androidgame.martin.myfirstgameengine.engine.sound

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer

import java.io.IOException

class Music(assetFileDescriptor: AssetFileDescriptor) : MediaPlayer.OnCompletionListener {
    private val mediaPlayer: MediaPlayer = MediaPlayer() // MediaPlayer doing the music playback
    private var isPrepared = false // is the MediaPlayer ready?

    val isPlaying: Boolean
        get() = mediaPlayer.isPlaying

    val isStopped: Boolean
        get() = !this.isPrepared

    var isLooping: Boolean
        get() = mediaPlayer.isLooping
        set(loop) {
            this.mediaPlayer.isLooping = loop
        }

    init {
        try {
            mediaPlayer.setDataSource(
                    assetFileDescriptor.fileDescriptor,
                    assetFileDescriptor.startOffset,
                    assetFileDescriptor.length
            )
            mediaPlayer.prepare()
            this.isPrepared = true
            mediaPlayer.setOnCompletionListener(this)
        } catch (e: IOException) {
            throw RuntimeException("Could not open the music file descriptor: $assetFileDescriptor")
        }

    }

    fun dispose() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
    }

    fun pause() {
        if (mediaPlayer.isPlaying) mediaPlayer.pause()
    }

    fun play() {
        if (mediaPlayer.isPlaying) return
        try {
            synchronized(this) {
                if (!isPrepared) mediaPlayer.prepare()
                mediaPlayer.start()
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            throw RuntimeException("Music class: You are trying to play from a wrong MediaPlayer.")
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException("MediaPlayer play error.")
        }

    }

    fun setVolume(volume: Float) {
        this.mediaPlayer.setVolume(volume, volume)
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        synchronized(this) {
            this.isPrepared = false
        }
    }
}
