package com.example.playlistmaker.utils

sealed interface ScreenState<T> {

    data class Content<T>(
        val data: T
    ): ScreenState<T>

    data object Empty: ScreenState<Nothing>

}