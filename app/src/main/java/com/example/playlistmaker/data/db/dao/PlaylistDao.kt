package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.SaveTrackEntity

@Dao
interface PlaylistDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_name ORDER BY id DESC" )
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Insert(entity = SaveTrackEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: SaveTrackEntity)

    @Query("SELECT * FROM save_track_name WHERE trackId = :id" )
    suspend fun getTrack(id: Long): SaveTrackEntity

    @Query("UPDATE playlist_name " +
            "SET trackList = CASE WHEN trackList IS NULL OR trackList = ''" +
            "                    THEN :trackId" +
            "                    ELSE trackList || ', ' || :trackId" +
            "               END," +
            "count = count + 1 " +
            "WHERE id = :playlistId")
    suspend fun addTrackToPlaylist(trackId: String, playlistId: Int)

    @Transaction
    suspend fun insertTrackAndPlaylist(track: SaveTrackEntity, trackId: String, playlistId: Int) {
        addTrackToPlaylist(trackId, playlistId)
        insertTrack(track)
    }

}