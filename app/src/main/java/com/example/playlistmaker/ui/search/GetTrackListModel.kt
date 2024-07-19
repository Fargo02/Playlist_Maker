package com.example.playlistmaker.ui.search

import com.example.playlistmaker.domain.search.model.Track

data class GetTrackListModel (
    val trackList: List<Track>,
    val isVisible: Boolean,
)