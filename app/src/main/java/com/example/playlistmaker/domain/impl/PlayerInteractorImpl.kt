package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.PlayerRepository

class PlayerInteractorImpl(
    private val playerRepository: PlayerRepository
) : PlayerInteractor{
    override fun prepare(path: String, listener: PlayerInteractor.OnStateChangeListener) {
        playerRepository.prepare(path, listener)
    }

    override fun play(listener: PlayerInteractor.OnStateChangeListener) {
        playerRepository.play(listener)
    }

    override fun pause(listener: PlayerInteractor.OnStateChangeListener) {
        playerRepository.pause(listener)
    }

}