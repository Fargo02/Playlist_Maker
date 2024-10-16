package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.HistoryTrackEntity

@Dao
interface HistoryTrackDao {

    @Query("DELETE FROM history_track_name")
    suspend fun deleteAllTracks()

    @Insert(entity = HistoryTrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: HistoryTrackEntity)

    @Query("SELECT * FROM history_track_name ORDER BY currentTime DESC" )
    suspend fun getTracks(): List<HistoryTrackEntity>
}