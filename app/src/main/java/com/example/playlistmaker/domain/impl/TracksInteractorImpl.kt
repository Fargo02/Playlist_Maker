package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        val t = Thread {
            val tracksList = repository.searchTracks(expression)
            val filteredTracksList = tracksList.filter { track -> track.previewUrl != null }
            consumer.consume(filteredTracksList)
        }
        t.start()
    }
}