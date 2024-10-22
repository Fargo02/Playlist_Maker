package com.example.playlistmaker.ui.create_playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.ui.playlist.model.CurrentPlaylist
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val playlistId: Long
): ViewModel() {

    private val playlistListener = MutableLiveData<Playlist>()
    fun observePlaylistListener(): LiveData<Playlist> = playlistListener

    fun getPlaylist() {
        viewModelScope.launch {
            playlistListener.value = playlistInteractor.getPlaylist(playlistId)
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(playlist)
        }
    }

    fun addPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.insertPlaylist(playlist)
        }
    }
}