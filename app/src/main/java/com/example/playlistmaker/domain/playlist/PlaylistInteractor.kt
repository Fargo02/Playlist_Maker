package com.example.playlistmaker.domain.playlist

import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun deletePlaylist(idPlaylist: Long)

    suspend fun deleteTrack(idTrack: Long, idPlaylist: Long)

    suspend fun getPlaylists(): Flow<List<Playlist>>

    suspend fun insertTrackAndPlaylist(track: Track, trackId: String, playlistId: Long)

    suspend fun getTrack(id: Long): Track

    suspend fun getPlaylist(id: Long): Playlist

    suspend fun updatePlaylist(playlist: Playlist)
}