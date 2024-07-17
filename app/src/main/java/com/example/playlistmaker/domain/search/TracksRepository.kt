package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.utils.Resource

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}