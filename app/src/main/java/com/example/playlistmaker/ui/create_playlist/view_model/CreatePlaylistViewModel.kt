package com.example.playlistmaker.ui.create_playlist.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.playlist.model.Playlist
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    fun addPlaylist(playlist: Playlist) {
        insertPlaylist(playlist)
    }

    private fun insertPlaylist(playlist: Playlist){
        viewModelScope.launch {
            playlistInteractor.insertPlaylist(playlist)
        }
    }
}