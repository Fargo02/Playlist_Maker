package com.example.playlistmaker.domain.player

interface PlayerRepository {
    fun prepare(path: String ,listener: PlayerInteractor.OnStateChangeListener)
    fun play(listener: PlayerInteractor.OnStateChangeListener)
    fun pause(listener: PlayerInteractor.OnStateChangeListener)
}