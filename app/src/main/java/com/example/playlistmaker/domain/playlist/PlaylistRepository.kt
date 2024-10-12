package com.example.playlistmaker.domain.playlist

import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun getPlaylists(): Flow<List<Playlist>>

    suspend fun insertTrackAndPlaylist(track: Track, trackId: String, playlistId: Int)

    suspend fun getTrack(id: Long): Track
}