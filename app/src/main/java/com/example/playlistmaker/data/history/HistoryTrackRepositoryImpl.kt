package com.example.playlistmaker.data.history

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.converter.HistoryTrackDbConverter
import com.example.playlistmaker.data.db.entity.HistoryTrackEntity
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.history.HistoryTrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HistoryTrackRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val converter: HistoryTrackDbConverter,
): HistoryTrackRepository {

    override suspend fun insertTrack(track: Track)  {
        appDatabase.historyTrackDao().insertTrack(converter.map(track))
    }

    override suspend fun deleteAllTracks() {
        appDatabase.historyTrackDao().deleteAllTracks()
    }

    override suspend fun getHistoryTracks(): Flow<List<Track>> = flow {
        val trackIdSet = appDatabase.trackFavouriteDao().getIdTracks().toSet()
        val tracks = appDatabase.historyTrackDao().getTracks()
        val result = tracks.map { track ->
            val isFavorite = trackIdSet.contains(track.trackId)
            track.copy(isFavorite = isFavorite)
        }
        emit(convertFromTrackEntity(result))
    }

    private fun convertFromTrackEntity(tracks: List<HistoryTrackEntity>): List<Track> {
        return tracks.map { track -> converter.map(track) }
    }
}