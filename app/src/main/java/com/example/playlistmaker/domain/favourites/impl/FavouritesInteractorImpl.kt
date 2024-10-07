package com.example.playlistmaker.domain.favourites.impl

import com.example.playlistmaker.domain.favourites.FavouritesInteractor
import com.example.playlistmaker.domain.favourites.FavouritesRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class FavouritesInteractorImpl(
    private val repository: FavouritesRepository
): FavouritesInteractor {
    override suspend fun insertTrack(track: Track) {
        repository.insertTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        repository.deleteTrack(track)
    }

    override suspend fun getFavouritesTracks(): Flow<List<Track>> {
        return repository.getFavouritesTracks()
    }

}