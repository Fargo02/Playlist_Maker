package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.utils.Resource

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        val t = Thread {
            when(val resource = repository.searchTracks(expression)) {
                is Resource.Success -> {
                    consumer.consume(
                        resource.data?.filter {
                            track -> track.previewUrl != null }, null
                    )
                }
                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }
            }
        }
        t.start()
    }
}