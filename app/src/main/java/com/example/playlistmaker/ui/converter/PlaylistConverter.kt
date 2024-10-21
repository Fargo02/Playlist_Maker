package com.example.playlistmaker.ui.converter

import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.ui.playlist.model.CurrentPlaylist

class PlaylistConverter() {

    fun map(playlist: Playlist, durationInMills: Long): CurrentPlaylist {

        val durationInMin = durationInMills / 60000

        val durationText: String = when(durationInMin) {
            1L -> "$durationInMin минута"
            2L, 3L, 4L ->  "$durationInMin минуты"
            else ->  "$durationInMin минут"
        }

        val countTracks = when(playlist.count % 10) {
            1 -> "${playlist.count} трек"
            2, 3, 4 -> "${playlist.count} трека"
            else -> "${playlist.count} треков"
        }

        return CurrentPlaylist(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            imageUri = playlist.imageUri,
            duration = durationText,
            count = countTracks
        )
    }
}