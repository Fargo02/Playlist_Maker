package com.example.playlistmaker.data.favourites

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.converter.TrackDbConverter
import com.example.playlistmaker.data.db.entity.TrackFavouriteEntity
import com.example.playlistmaker.domain.favourites.FavouritesRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

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

    override suspend fun getFavouritesTracks(): Flow<List<Track>> {
        val tracks = appDatabase.trackFavouriteDao().getTracks()
        return convertFromTrackEntity(tracks)
    }

    private fun convertFromTrackEntity(tracks: Flow<List<TrackFavouriteEntity>>): Flow<List<Track>> {
        return tracks.map { favoriteTrackEntities ->
            favoriteTrackEntities.map { favoriteTrackEntity ->
                trackDbConvertor.map(favoriteTrackEntity)
            }
        }
    }
}