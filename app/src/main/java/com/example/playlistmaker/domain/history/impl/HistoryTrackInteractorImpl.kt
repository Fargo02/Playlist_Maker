package com.example.playlistmaker.domain.history.impl

import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.history.HistoryTrackInteractor
import com.example.playlistmaker.domain.history.HistoryTrackRepository
import kotlinx.coroutines.flow.Flow

class HistoryTrackInteractorImpl(
    private val repository: HistoryTrackRepository
): HistoryTrackInteractor {

    override suspend fun insertTrack(track: Track) {
        repository.insertTrack(track)
    }

    override suspend fun deleteAllTracks() {
        repository.deleteAllTracks()
    }

    override suspend fun getHistoryTracks(): Flow<List<Track>> {
        return repository.getHistoryTracks()
    }

}