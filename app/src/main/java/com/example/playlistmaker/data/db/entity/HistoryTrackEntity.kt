package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmaker.data.db.entity.HistoryTrackEntity.Companion.TABLE_HISTORY_NAME

@Entity(tableName = TABLE_HISTORY_NAME)
data class HistoryTrackEntity(
    @PrimaryKey
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val collectionName: String? = null,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String? = null,
    var isFavorite: Boolean,
    val currentTime: String,
) {
    companion object {
        const val TABLE_HISTORY_NAME = "history_track_name"
    }
}