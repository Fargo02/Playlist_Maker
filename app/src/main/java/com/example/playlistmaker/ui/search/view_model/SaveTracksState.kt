package com.example.playlistmaker.ui.search.view_model

import com.example.playlistmaker.ui.search.GetTrackListModel

sealed interface SaveTracksState {

    data class Content(
        val tracks: GetTrackListModel
    ) : SaveTracksState

    data object Empty : SaveTracksState
}