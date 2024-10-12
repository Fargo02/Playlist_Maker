package com.example.playlistmaker.ui.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    fun fillData() {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect {
                    processResult(it)
                }
        }
    }

    private val stateLiveData = MutableLiveData<PlaylistState>()
    fun observerStateLiveData() : LiveData<PlaylistState> = stateLiveData

    private fun processResult(foundTracks: List<Playlist>?) {

        renderState(
            if (foundTracks.isNullOrEmpty()) PlaylistState.Empty
            else PlaylistState.Content(playlists = foundTracks)
        )

    }

    private fun renderState(state: PlaylistState) {
        stateLiveData.postValue(state)
    }
}