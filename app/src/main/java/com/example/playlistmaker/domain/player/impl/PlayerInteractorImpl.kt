package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.PlayerRepository

class PlayerInteractorImpl(
    private val playerRepository: PlayerRepository
) : PlayerInteractor{
    override fun prepare(path: String, listener: PlayerInteractor.OnStateChangeListener) {
        playerRepository.prepare(path, listener)
    }

    override fun play() {
        playerRepository.play()
    }

    override fun pause() {
        playerRepository.pause()
    }

    override fun getCurrentTime(): String {
        return playerRepository.getCurrentTime()
    }

    override fun release() {
        playerRepository.release()
    }

    override fun isPlaying(): Boolean {
        return playerRepository.isPlaying()
    }

}