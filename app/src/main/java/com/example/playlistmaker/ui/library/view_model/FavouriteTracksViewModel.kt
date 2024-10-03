package com.example.playlistmaker.ui.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favourites.FavouritesInteractor
import com.example.playlistmaker.domain.player.PlayerState
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.search.view_model.SearchState
import kotlinx.coroutines.launch

class FavouriteTracksViewModel(
    private val favouritesInteractor: FavouritesInteractor
): ViewModel() {

    fun fillData() {
        viewModelScope.launch {
            favouritesInteractor
                .getFavouritesTracks()
                .collect {
                    processResult(it)
                }
        }
    }

    private val favouriteState = MutableLiveData<FavouriteState>()
    fun observeFavouriteState(): LiveData<FavouriteState> = favouriteState


    private fun processResult(foundTracks: List<Track>?) {

        renderState(
            if (foundTracks.isNullOrEmpty()) FavouriteState.Empty
            else FavouriteState.Content(tracks = foundTracks)
        )

    }

    private fun renderState(state: FavouriteState) {
        favouriteState.postValue(state)
    }
}