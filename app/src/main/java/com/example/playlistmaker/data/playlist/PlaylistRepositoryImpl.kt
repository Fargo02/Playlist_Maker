package com.example.playlistmaker.data.playlist

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.converter.PlaylistDbConverter
import com.example.playlistmaker.data.db.entity.PlaylistEntity
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

    override suspend fun deletePlaylist(idPlaylist: Long) {
        database.playlistDao().deletePlaylist(idPlaylist)
    }

    override suspend fun deleteTrack(idTrack: Long, idPlaylist: Long) {
        val listTracks = database.playlistDao().getPlaylist(idPlaylist).trackList
        database.playlistDao().deleteTrackFromPlaylist(idPlaylist, removeNumberFromString(listTracks, idTrack))
        checkTrackInDatabase(idTrack)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = database.playlistDao().getPlaylists()
        emit(convertFromPlaylistsEntity(playlists))
    }

    override suspend fun insertTrackAndPlaylist(track: Track, trackId: String, playlistId: Long) {
        database.playlistDao().insertTrackAndPlaylist(converter.map(track), trackId, playlistId)
    }

    override suspend fun getTrack(id: Long): Track {
        val track = converter.map(database.playlistDao().getTrack(id))
        return track
    }

    override suspend fun getPlaylist(id: Long): Playlist {
        val playlist = converter.map(database.playlistDao().getPlaylist(id))
        return playlist
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        database.playlistDao().updatePlaylist(converter.map(playlist))
    }

    private fun convertFromPlaylistsEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> converter.map(playlist) }
    }

    private fun removeNumberFromString(originalString: String, numberToRemove: Long): String {
        val numbersList = originalString.split(",").map { it.toLong() }
        val filteredList = numbersList.filter { it != numberToRemove }
        return filteredList.joinToString(",")
    }

    private suspend fun checkTrackInDatabase(idTrack: Long) {
        var deleteTrack = true
        val playlists = database.playlistDao().getPlaylists()
        playlists.forEach { playlist ->
            if (playlist.trackList.contains(idTrack.toString())) {
                deleteTrack = false
            }
        }
        if (deleteTrack) database.playlistDao().deleteTrack(idTrack)
    }
}