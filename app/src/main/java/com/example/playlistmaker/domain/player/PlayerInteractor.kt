package com.example.playlistmaker.domain.player

interface PlayerInteractor {
    fun prepare(path: String ,listener: OnStateChangeListener)
    fun play(listener: OnStateChangeListener)
    fun pause(listener: OnStateChangeListener)
    interface OnStateChangeListener {
        fun onChange(state : PlayerState)
    }
}