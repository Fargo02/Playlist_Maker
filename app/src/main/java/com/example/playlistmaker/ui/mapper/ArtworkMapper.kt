package com.example.playlistmaker.ui.mapper

object ArtworkMapper {
    fun getCoverArtwork(url : String) = url.replaceAfterLast('/',"512x512bb.jpg")
}