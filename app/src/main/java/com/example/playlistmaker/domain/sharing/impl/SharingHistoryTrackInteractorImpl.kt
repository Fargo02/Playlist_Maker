package com.example.playlistmaker.domain.sharing.impl

import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.sharing.history.SharingHistoryTrackInteractor
import com.example.playlistmaker.domain.sharing.history.SharingHistoryTrackRepository

class SharingHistoryTrackInteractorImpl(
    private val repository: SharingHistoryTrackRepository
): SharingHistoryTrackInteractor {
    override suspend fun getList(): List<Track> {
        return repository.getList()
    }

    override fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }

}