package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.HistoryTrackDao
import com.example.playlistmaker.data.db.dao.TrackFavouriteDao
import com.example.playlistmaker.data.db.entity.HistoryTrackEntity
import com.example.playlistmaker.data.db.entity.TrackEntity

@Database(version = 1, entities = [TrackEntity::class, HistoryTrackEntity::class])
abstract class AppDatabase: RoomDatabase() {

    abstract fun trackFavouriteDao(): TrackFavouriteDao

    abstract fun historyTrackDao(): HistoryTrackDao

}