package com.example.playlistmaker.data.db.converter

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.SaveTrackEntity
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            name = playlist.name,
            description = playlist.description,
            imageUri = playlist.imageUri,
            trackList = playlist.trackList,
            count = playlist.count
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            imageUri = playlist.imageUri,
            trackList = playlist.trackList,
            count = playlist.count
        )
    }

    fun map(track: Track): SaveTrackEntity {
        return SaveTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    fun map(track: SaveTrackEntity): Track {
        return Track(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }
}