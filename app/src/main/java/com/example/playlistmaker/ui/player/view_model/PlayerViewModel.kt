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
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favouritesInteractor: FavouritesInteractor,
    private val playlistInteractor: PlaylistInteractor,
    url: String
): ViewModel() {

    private var timerJob: Job? = null

    var listener = object : PlayerInteractor.OnStateChangeListener {
        override fun onChange(state: PlayerState) {
            playerStateListener.value = state
            when (state) {
                PREPARED -> {
                    playerInteractor.play()
                }
                PLAYING -> {
                    playerInteractor.play()
                }
                PAUSED -> {
                    timerJob?.cancel()
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

    private val currentTimeListener = MutableLiveData<String>()
    fun observeCurrentTimeListener(): LiveData<String> = currentTimeListener

    private val playlistStateListener = MutableLiveData<PlaylistState>()
    fun observePlaylistStateListener(): LiveData<PlaylistState> = playlistStateListener


    fun insertTrack(track: Track, trackList: String, playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor.insertTrackAndPlaylist(track, trackList, playlistId)
        }
    }

    fun getPlaylist() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .collect { processResult(it) }
        }
    }

    private fun processResult(foundPlaylist: List<Playlist>?) {

        renderState(
            if (foundPlaylist.isNullOrEmpty()) PlaylistState.Empty
            else PlaylistState.Content(playlists = foundPlaylist)
        )

    }

    private fun renderState(state: PlaylistState) {
        playlistStateListener.postValue(state)
    }

    fun updateCurrentTime() {
        viewModelScope.launch(Dispatchers.Main) {
            while (playerInteractor.isPlaying()) {
                val currentTime = playerInteractor.getCurrentTime()
                currentTimeListener.value = currentTime
                delay(DELAY)
            }
        }
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

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
    }

    companion object {
        private const val DELAY = 400L
    }

}