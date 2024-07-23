package com.example.playlistmaker.data.player

import android.media.MediaPlayer
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.player.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl(): PlayerRepository {

    private var mediaPlayer = MediaPlayer()
    private lateinit var listener: PlayerInteractor.OnStateChangeListener

    override fun prepare(path: String, listener: PlayerInteractor.OnStateChangeListener) {
        this.listener = listener
        mediaPlayer.setDataSource(path)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            listener.onChange(PlayerState.DEFAULT)
        }
        mediaPlayer.setOnCompletionListener {
            listener.onChange(PlayerState.DEFAULT)
        }
    }

    override fun play() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun getCurrentTime(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

}