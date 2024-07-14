package com.example.playlistmaker.ui.player.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.PlayerState
import com.example.playlistmaker.domain.player.PlayerState.*
import com.example.playlistmaker.utils.Creator

class PlayerViewModel(url: String): ViewModel() {

    var listener:  PlayerInteractor.OnStateChangeListener

    companion object {
        fun factory(url: String) = viewModelFactory {
            initializer {
                PlayerViewModel(url)
            }
        }
    }

    val playerInteractor = Creator.providePlayerInteractor()

    init {
        listener = object : PlayerInteractor.OnStateChangeListener {
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
        playerInteractor.prepare(url, listener)
    }

    private val stateListener = MutableLiveData<PlayerState>()
    fun observeStateListener(): LiveData<PlayerState> = stateListener

}