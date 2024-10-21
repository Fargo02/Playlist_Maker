package com.example.playlistmaker.ui.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.utils.ScreenState
import kotlinx.coroutines.launch

class PlaylistListViewModel(
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

    private val stateLiveData = MutableLiveData<ScreenState<out List<Playlist>>>()
    fun observerStateLiveData() : LiveData<ScreenState<out List<Playlist>>> = stateLiveData

    private fun processResult(foundTracks: List<Playlist>?) {

        renderState(
            if (foundTracks.isNullOrEmpty()) ScreenState.Empty
            else ScreenState.Content(foundTracks)
        )

    }

    private fun renderState(state: ScreenState<out List<Playlist>>) {
        stateLiveData.postValue(state)
    }
}