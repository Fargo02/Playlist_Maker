package com.example.playlistmaker.domain.history

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface HistoryTrackInteractor {

    suspend fun insertTrack(track: Track)

    suspend fun deleteAllTracks()

    suspend fun getHistoryTracks(): Flow<List<Track>>
}