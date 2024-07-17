package com.example.playlistmaker.domain.player

interface PlayerRepository {

    fun prepare(path: String ,listener: PlayerInteractor.OnStateChangeListener)

    fun play()

    fun pause()

    fun getCurrentTime(): String

    fun release()

    fun isPlaying(): Boolean

}