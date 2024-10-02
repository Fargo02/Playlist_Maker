package com.example.playlistmaker.data.sharing

import android.content.SharedPreferences
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.sharing.history.SharingHistoryTrackRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val HISTORY_SIZE = 10
private const val TRACK_FROM_HISTORY = "historyTrack"

class SharingHistoryTrackRepositoryImpl(
    private val sharedPreferences : SharedPreferences,
    private val appDatabase: AppDatabase,
): SharingHistoryTrackRepository {

    private var historyList: ArrayList<Track> = readSearchHistory()

    override suspend fun getList(): List<Track> {
        val trackIdSet = appDatabase.trackDao().getIdTracks().toSet()
        return historyList.map { track ->
            val isFavorite = trackIdSet.contains(track.trackId)
            track.copy(isFavorite = isFavorite)
        }
    }

    override fun addTrack(track: Track) {
        if(historyList.isEmpty()) {
            historyList.add(0, track)
            writeSearchHistory(historyList)
            return
        }
        historyList.forEach { item ->
            if (item.trackId == track.trackId) {
                historyList.remove(track)
                historyList.add(0, track)
                writeSearchHistory(historyList)
                return
            }
        }
        if (historyList.size >= HISTORY_SIZE) {
            historyList.removeLast()
            historyList.add(0, track)
            writeSearchHistory(historyList)
            return
        } else {
            historyList.add(0, track)
            writeSearchHistory(historyList)
            return
        }
    }

    override fun clearHistory() {
        historyList.clear()
        writeSearchHistory(historyList)
    }

    private fun readSearchHistory(): ArrayList<Track> {
        val json = sharedPreferences.getString(TRACK_FROM_HISTORY, null) ?: return ArrayList()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return Gson().fromJson(json, type)
    }


    private fun writeSearchHistory(historyList: ArrayList<Track>){
        val json = Gson().toJson(historyList)
        sharedPreferences.edit()
            .putString(TRACK_FROM_HISTORY, json)
            .apply()
    }
}