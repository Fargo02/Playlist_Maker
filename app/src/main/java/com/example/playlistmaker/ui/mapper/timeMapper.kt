package com.example.playlistmaker.ui.mapper

fun timeMapper(timeString: String): Long {
    val parts = timeString.split(":")
    if (parts.size != 2) {
        throw IllegalArgumentException("Неверный формат: $timeString")
    }

    val minutes = parts[0].toInt()
    val seconds = parts[1].toInt()

    return minutes * 60000L + seconds * 1000L
}