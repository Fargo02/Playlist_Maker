package com.example.playlistmaker.domain.sharing.history

import com.example.playlistmaker.domain.search.model.Track

interface SharingHistoryTrackRepository {

    suspend fun getList(): List<Track>

    fun addTrack(track: Track)

    fun clearHistory()

}