package com.example.playlistmaker.ui.search.view_model

import com.example.playlistmaker.domain.search.model.Track

sealed interface SearchState {
    data object Loading : SearchState

    data class Content(
        val tracks: List<Track>
    ) : SearchState

    data object Error : SearchState

    data object Empty : SearchState
}