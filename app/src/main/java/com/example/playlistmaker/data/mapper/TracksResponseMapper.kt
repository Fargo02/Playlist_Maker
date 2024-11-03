package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.search.dto.TracksSearchResponse
import com.example.playlistmaker.domain.search.model.Track
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TracksResponseMapper(
    private val appDatabase: AppDatabase,
    private val firstApiFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
) {
    suspend fun map(response: TracksSearchResponse): List<Track> {
        val trackIdSet = appDatabase.trackFavouriteDao().getIdTracks().toSet()
        return response.results.map { track ->
            Track(
                trackName = track.trackName,
                artistName = track.artistName,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl100 = track.artworkUrl100,
                trackId = track.trackId,
                collectionName = track.collectionName,
                releaseDate = if (track.releaseDate != null) LocalDate.parse(track.releaseDate, firstApiFormat).year.toString() else "",
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl,
                isFavorite = trackIdSet.contains(track.trackId)
            )
        }
    }
}