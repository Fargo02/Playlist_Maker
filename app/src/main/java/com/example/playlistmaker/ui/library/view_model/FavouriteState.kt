package com.example.playlistmaker.ui.library.view_model

import com.example.playlistmaker.domain.search.model.Track

sealed interface FavouriteState {
    data class Content(
        val tracks: List<Track>
    ) : FavouriteState

    data object Empty : FavouriteState
}