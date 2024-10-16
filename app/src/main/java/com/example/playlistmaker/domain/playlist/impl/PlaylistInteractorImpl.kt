package com.example.playlistmaker.domain.playlist.impl

import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.playlist.PlaylistRepository
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
): PlaylistInteractor {

    override suspend fun insertPlaylist(playlist: Playlist) {
        repository.insertPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun insertTrackAndPlaylist(track: Track, trackId: String, playlistId: Int) {
        repository.insertTrackAndPlaylist(track, trackId, playlistId)
    }

    override suspend fun getTrack(id: Long): Track {
        return repository.getTrack(id)
    }

}