package com.example.playlistmaker.data.playlist

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.converter.PlaylistDbConverter
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.SaveTrackEntity
import com.example.playlistmaker.domain.playlist.PlaylistRepository
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val database: AppDatabase,
    private val converter: PlaylistDbConverter,
): PlaylistRepository {

    override suspend fun insertPlaylist(playlist: Playlist) {
        database.playlistDao().insertPlaylist(converter.map(playlist))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        database.playlistDao().deletePlaylist(converter.map(playlist))
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = database.playlistDao().getPlaylists()
        emit(convertFromPlaylistsEntity(playlists))
    }

    override suspend fun insertTrackAndPlaylist(track: Track, trackId: String, playlistId: Int) {
        database.playlistDao().insertTrackAndPlaylist(converter.map(track), trackId, playlistId)
    }

    override suspend fun getTrack(id: Long): Track {
        val track = converter.map(database.playlistDao().getTrack(id))
        return track
    }

    private fun convertFromPlaylistsEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> converter.map(playlist) }
    }
}