package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.HistoryTrackEntity
import com.example.playlistmaker.data.db.entity.HistoryTrackEntity.Companion.TABLE_HISTORY_NAME

@Dao
interface HistoryTrackDao {

    @Query("DELETE FROM $TABLE_HISTORY_NAME")
    suspend fun deleteAllTracks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: HistoryTrackEntity)

    @Query("SELECT * FROM $TABLE_HISTORY_NAME ORDER BY currentTime DESC" )
    suspend fun getTracks(): List<HistoryTrackEntity>
}