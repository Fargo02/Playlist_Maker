package com.example.playlistmaker

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
private const val HISTORY_SIZE = 10
class SearchHistory(private val sharedPreferences : SharedPreferences) {
    private var historyList: ArrayList<Track> = readSearchHistory()
    fun addTrack(track: Track) {
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
    fun getList(): List<Track> {
        return historyList
    }
    fun clearHistory() {
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