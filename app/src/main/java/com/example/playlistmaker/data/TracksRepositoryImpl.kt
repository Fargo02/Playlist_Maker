package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    private val timeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val firstApiFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return if (response.resultCode == 200) {
            (response as TracksSearchResponse).results.map {
                Track(
                    it.trackName,
                    it.artistName,
                    timeFormat.format(it.trackTimeMillis.toLong()),
                    it.artworkUrl100,
                    it.trackId,
                    it.collectionName,
                    if (it.releaseDate != null) LocalDate.parse(it.releaseDate , firstApiFormat).year.toString() else "",
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl) }
        } else {
            emptyList()
        }
    }
}