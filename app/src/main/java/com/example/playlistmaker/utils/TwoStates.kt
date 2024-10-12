package com.example.playlistmaker.utils

sealed interface TwoStates<T> {

    data class Content<T>(
        val data: T
    ): TwoStates<T>

    data object Empty: TwoStates<Nothing>

}