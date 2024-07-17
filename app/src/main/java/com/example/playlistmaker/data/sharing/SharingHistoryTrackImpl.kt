package com.example.playlistmaker.data.sharing

import android.content.SharedPreferences
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.sharing.history.SharingHistoryTrackRepository
import com.example.playlistmaker.ui.search.activity.TRACK_FROM_HISTORY
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val HISTORY_SIZE = 10

class SharingHistoryTrackImpl(
    private val sharedPreferences : SharedPreferences
): SharingHistoryTrackRepository {
    private var historyList: ArrayList<Track> = readSearchHistory()

    override fun getList(): List<Track> {
        return historyList
    }

    override fun addTrack(track: Track) {
        if(historyList.isEmpty()) {
            historyList.add(0, track)
            writeSearchHistory(historyList)
            return
        }
        historyList.forEach { item ->
            if (item.trackId.equals(track.trackId)) {
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