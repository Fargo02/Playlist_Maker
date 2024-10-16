package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.HistoryTrackDao
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.TrackFavouriteDao
import com.example.playlistmaker.data.db.entity.HistoryTrackEntity
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.SaveTrackEntity
import com.example.playlistmaker.data.db.entity.TrackFavouriteEntity

@Database(version = 1, entities = [
    TrackFavouriteEntity::class,
    HistoryTrackEntity::class,
    PlaylistEntity::class,
    SaveTrackEntity::class
])
abstract class AppDatabase: RoomDatabase() {

    abstract fun trackFavouriteDao(): TrackFavouriteDao

    abstract fun historyTrackDao(): HistoryTrackDao

    abstract fun playlistDao(): PlaylistDao

}