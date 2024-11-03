package com.example.playlistmaker.ui.mapper

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TimeMapper() {
    fun getLong(timeString: String): Long {
        val parts = timeString.split(":")
        if (parts.size != 2) {
            throw IllegalArgumentException("Неверный формат: $timeString")
        }

        val minutes = parts[0].toInt()
        val seconds = parts[1].toInt()

        return minutes * 60000L + seconds * 1000L
    }

    fun getString(time: String): String {
        val timeFormat: DateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        return timeFormat.format(time.toLong())
    }
}
