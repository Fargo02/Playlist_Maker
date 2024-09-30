package com.example.playlistmaker.data.search

import com.example.playlistmaker.data.search.dto.TracksSearchRequest
import com.example.playlistmaker.data.search.dto.TracksSearchResponse
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {

    private val timeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val firstApiFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 -> {
                with(response as TracksSearchResponse) {
                    val data = results.map {
                        Track(
                            it.trackName,
                            it.artistName,
                            timeFormat.format(it.trackTimeMillis.toLong()),
                            it.artworkUrl100,
                            it.trackId,
                            it.collectionName,
                            if (it.releaseDate != null) LocalDate.parse(
                                it.releaseDate,
                                firstApiFormat
                            ).year.toString() else "",
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl
                        )
                    }
                    emit(Resource.Success(data))
                }
            }
            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}