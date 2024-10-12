package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackFavouriteEntity

@Dao
interface TrackFavouriteDao {

    @Insert(entity = TrackFavouriteEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackFavouriteEntity)

    @Delete
    suspend fun deleteTrack(track: TrackFavouriteEntity)

    @Query("SELECT * FROM track_favourite_name ORDER BY currentTime DESC" )
    suspend fun getTracks(): List<TrackFavouriteEntity>

    @Query("SELECT trackId FROM track_favourite_name")
    suspend fun getIdTracks(): List<Long>
}