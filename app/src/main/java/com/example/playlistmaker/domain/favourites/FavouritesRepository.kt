package com.example.playlistmaker.domain.favourites

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavouritesRepository {

    suspend fun insertTrack(track: Track)

    suspend fun deleteTrack(track: Track)

    suspend fun getFavouritesTracks(): Flow<List<Track>>
}