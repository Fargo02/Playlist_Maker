package com.example.playlistmaker.data.favourites

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.converter.TrackDbConverter
import com.example.playlistmaker.data.db.entity.TrackFavouriteEntity
import com.example.playlistmaker.domain.favourites.FavouritesRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavouritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConverter,
): FavouritesRepository {

    override suspend fun insertTrack(track: Track)  {
        appDatabase.trackFavouriteDao().insertTrack(trackDbConvertor.map(track))
    }

    override suspend fun deleteTrack(track: Track) {
        appDatabase.trackFavouriteDao().deleteTrack(trackDbConvertor.map(track))
    }

    override suspend fun getFavouritesTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackFavouriteDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackFavouriteEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

}