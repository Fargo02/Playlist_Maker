package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.db.entity.TrackEntity.Companion.TABLE_NAME

@Dao
interface TrackFavouriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM $TABLE_NAME ORDER BY currentTime DESC" )
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM $TABLE_NAME")
    suspend fun getIdTracks(): List<Long>
}