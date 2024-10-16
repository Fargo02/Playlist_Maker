package com.example.playlistmaker.ui.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favourites.FavouritesInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.utils.ScreenState
import kotlinx.coroutines.launch

class FavouriteTracksViewModel(
    private val favouritesInteractor: FavouritesInteractor
): ViewModel() {

    fun fillData() {
        viewModelScope.launch {
            favouritesInteractor
                .getFavouritesTracks()
                .collect { processResult(it) }
        }
    }

    private val favouriteState = MutableLiveData<ScreenState<out List<Track>>>()
    fun observeFavouriteState(): LiveData<ScreenState<out List<Track>>> = favouriteState


    private fun processResult(foundTracks: List<Track>?) {
        renderState(
            if (foundTracks.isNullOrEmpty()) ScreenState.Empty
            else ScreenState.Content(foundTracks)
        )
    }

    private fun renderState(state: ScreenState<out List<Track>>) {
        favouriteState.postValue(state)
    }
}