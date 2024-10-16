package com.example.playlistmaker.data.db.converter

import com.example.playlistmaker.data.db.entity.TrackFavouriteEntity
import com.example.playlistmaker.domain.search.model.Track
import java.time.LocalDateTime

class TrackDbConverter {

    fun map(track: Track): TrackFavouriteEntity {
        return TrackFavouriteEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavorite = track.isFavorite,
            currentTime = LocalDateTime.now().toString()
        )
    }

    fun map(track: TrackFavouriteEntity): Track {
        return Track(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            trackId = track.trackId,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavorite = track.isFavorite
        )
    }


 }