package com.example.playlistmaker.ui.playlist.model

data class CurrentPlaylist(
    val id: Long,
    val name: String,
    val description: String = "",
    val imageUri: String = "",
    val duration: String,
    val count: String = "0",
)
