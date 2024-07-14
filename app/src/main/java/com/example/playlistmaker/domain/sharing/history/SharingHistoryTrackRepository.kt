package com.example.playlistmaker.domain.sharing.history

import com.example.playlistmaker.domain.search.model.Track

interface SharingHistoryTrackRepository {

    fun getList(): List<Track>

    fun addTrack(track: Track)

    fun clearHistory()

}