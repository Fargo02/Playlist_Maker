package com.example.playlistmaker.domain.player

interface PlayerInteractor {

    fun prepare(path: String ,listener: OnStateChangeListener)

    fun play()

    fun pause()

    fun getCurrentTime(): String

    fun release()

    fun isPlaying(): Boolean

    interface OnStateChangeListener {
        fun onChange(state : PlayerState)
    }
}