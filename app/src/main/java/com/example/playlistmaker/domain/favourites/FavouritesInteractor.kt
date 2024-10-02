package com.example.playlistmaker.domain.favourites

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavouritesInteractor {

    suspend fun insertTrack(track: Track)

    suspend fun deleteTrack(track: Track)

    suspend fun getFavouritesTracks(): Flow<List<Track>>

}