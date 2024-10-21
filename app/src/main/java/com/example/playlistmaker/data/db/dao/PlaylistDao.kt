package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.SaveTrackEntity

@Dao
interface PlaylistDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Insert(entity = SaveTrackEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: SaveTrackEntity)

    @Update(entity = PlaylistEntity::class)
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlist_name WHERE id = :idPlaylist")
    suspend fun deletePlaylist(idPlaylist: Long)

    @Query("DELETE FROM save_track_name WHERE trackId =:id")
    suspend fun deleteTrack(id: Long)

    @Transaction
    @Query("""
        UPDATE playlist_name
        SET trackList = :newTrackList, count = count - 1
        WHERE id = :idPlaylist
        """)
    suspend fun deleteTrackFromPlaylist(idPlaylist: Long, newTrackList: String)

    @Query("SELECT * FROM playlist_name ORDER BY id DESC" )
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlist_name WHERE id = :id")
    suspend fun getPlaylist(id: Long): PlaylistEntity

    @Query("SELECT * FROM save_track_name WHERE trackId = :id" )
    suspend fun getTrack(id: Long): SaveTrackEntity

    @Query("""
        UPDATE playlist_name 
        SET trackList = CASE 
        WHEN trackList IS NULL 
        OR trackList = '' 
        THEN :trackId 
        ELSE trackList || ',' || :trackId END, 
        count = count + 1 WHERE id = :playlistId
        """)
    suspend fun addTrackToPlaylist(trackId: String, playlistId: Long)

    @Transaction
    suspend fun insertTrackAndPlaylist(track: SaveTrackEntity, trackId: String, playlistId: Long) {
        addTrackToPlaylist(trackId, playlistId)
        insertTrack(track)
    }



}