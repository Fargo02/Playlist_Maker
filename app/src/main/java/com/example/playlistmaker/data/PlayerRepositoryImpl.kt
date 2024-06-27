package com.example.playlistmaker.data

import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.player.PlayerState

class PlayerRepositoryImpl(): PlayerRepository{
    private var mediaPlayer = MediaPlayer()
    private lateinit var listener: PlayerInteractor.OnStateChangeListener
    override fun prepare(path: String, listener: PlayerInteractor.OnStateChangeListener) {
        Log.i("mediaPlayer", "start prepare")
        this.listener = listener
        mediaPlayer.setDataSource(path)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            Log.i("mediaPlayer", "prepared")
            listener.onChange(PlayerState.PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            listener.onChange(PlayerState.PREPARED)
        }
    }
    override fun play(listener: PlayerInteractor.OnStateChangeListener) {
        Log.i("mediaPlayer", "started")
        this.listener = listener
        mediaPlayer.start()
        listener.onChange(PlayerState.PLAYING)
    }
    override fun pause(listener: PlayerInteractor.OnStateChangeListener) {
        Log.i("mediaPlayer", "paused")
        this.listener = listener
        mediaPlayer.pause()
        listener.onChange(PlayerState.PAUSED)
    }
}