package com.example.playlistmaker.ui.search.view_model

import com.example.playlistmaker.domain.search.model.Track

sealed interface HistoryState {

    data class Content(
        val tracks: List<Track>
    ) : HistoryState

    data object Empty : HistoryState
}