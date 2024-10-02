package com.example.playlistmaker.ui.player.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favourites.FavouritesInteractor
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.PlayerState
import com.example.playlistmaker.domain.player.PlayerState.DEFAULT
import com.example.playlistmaker.domain.player.PlayerState.PAUSED
import com.example.playlistmaker.domain.player.PlayerState.PLAYING
import com.example.playlistmaker.domain.player.PlayerState.PREPARED
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.library.view_model.FavouriteState
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favouritesInteractor: FavouritesInteractor,
    url: String
): ViewModel() {

    var listener = object : PlayerInteractor.OnStateChangeListener {
        override fun onChange(state: PlayerState) {
            playerStateListener.postValue(state)
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

    private val playerStateListener = MutableLiveData<PlayerState>()
    fun observePlayerStateListener(): LiveData<PlayerState> = playerStateListener

    private val favouriteState = MutableLiveData<Boolean>()
    fun observeFavouriteState(): LiveData<Boolean> = favouriteState

    fun getCurrentTime(): String {
        return playerInteractor.getCurrentTime()
    }

    fun getRelease() {
        playerInteractor.release()
    }

    fun isPlaying(): Boolean {
        return playerInteractor.isPlaying()
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            if (track.isFavorite) {
                favouritesInteractor.deleteTrack(track.copy(isFavorite = false))
                renderState(false)
            } else {
                favouritesInteractor.insertTrack(track.copy(isFavorite = true))
                renderState(true)
            }
        }
    }
    private fun renderState(isFavourite: Boolean) {
        favouriteState.postValue(isFavourite)
    }
}