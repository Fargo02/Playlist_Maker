package com.example.playlistmaker.ui.player.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.PlayerState
import com.example.playlistmaker.domain.player.PlayerState.DEFAULT
import com.example.playlistmaker.domain.player.PlayerState.PAUSED
import com.example.playlistmaker.domain.player.PlayerState.PLAYING
import com.example.playlistmaker.domain.player.PlayerState.PREPARED

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    url: String
): ViewModel() {

    var listener = object : PlayerInteractor.OnStateChangeListener {
        override fun onChange(state: PlayerState) {
            stateListener.postValue(state)
            when (state) {
                PREPARED -> {
                    playerInteractor.play()
                }
                PLAYING -> {
                    playerInteractor.play()
                }
                PAUSED -> {
                    playerInteractor.pause()
                }
                DEFAULT -> Log.i("playerState", "$state")
            }
        }
    }

    init {
        playerInteractor.prepare(url, listener)
    }

    private val stateListener = MutableLiveData<PlayerState>()
    fun observeStateListener(): LiveData<PlayerState> = stateListener

    fun getCurrentTime(): String {
        return playerInteractor.getCurrentTime()
    }

    fun getRelease() {
        playerInteractor.release()
    }

    fun isPlaying(): Boolean {
        return playerInteractor.isPlaying()
    }
}