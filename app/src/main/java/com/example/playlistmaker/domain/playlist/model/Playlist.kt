package com.example.playlistmaker.domain.playlist.model

data class Playlist(
    val id: Long,
    val name: String,
    val description: String = "",
    val imageUri: String = "",
    val trackList: String = "",
    val count: Int = 0,
)