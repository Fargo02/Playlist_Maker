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

    override suspend fun deletePlaylist(idPlaylist: Long) {
        repository.deletePlaylist(idPlaylist)
    }

    override suspend fun deleteTrack(idTrack: Long, idPlaylist: Long) {
        repository.deleteTrack(idTrack, idPlaylist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun insertTrackAndPlaylist(track: Track, trackId: String, playlistId: Long) {
        repository.insertTrackAndPlaylist(track, trackId, playlistId)
    }

    override suspend fun getTrack(id: Long): Track {
        return repository.getTrack(id)
    }

    override suspend fun getPlaylist(id: Long): Playlist {
        return repository.getPlaylist(id)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        return repository.updatePlaylist(playlist)
    }
}