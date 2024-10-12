package com.example.playlistmaker.ui.library.view_model

import com.example.playlistmaker.domain.playlist.model.Playlist

sealed interface PlaylistState {

    data object Empty: PlaylistState

    data class Content(
        val playlists: List<Playlist>
    ): PlaylistState

}