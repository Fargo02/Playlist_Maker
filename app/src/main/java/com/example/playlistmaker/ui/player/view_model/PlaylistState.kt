package com.example.playlistmaker.ui.player.view_model

import com.example.playlistmaker.domain.playlist.model.Playlist

sealed interface PlaylistState {

    data class Content(
        val playlists: List<Playlist>
    ) :PlaylistState

    data object Empty: PlaylistState
}