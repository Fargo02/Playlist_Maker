package com.example.playlistmaker.di

import com.example.playlistmaker.data.player.PlayerRepositoryImpl
import com.example.playlistmaker.data.search.TracksRepositoryImpl
import com.example.playlistmaker.data.sharing.SharingHistoryTrackRepositoryImpl
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.sharing.history.SharingHistoryTrackRepository
import org.koin.dsl.module

val repositoryModule = module {

    factory<PlayerRepository> {
        PlayerRepositoryImpl()
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<SharingHistoryTrackRepository> {
        SharingHistoryTrackRepositoryImpl(get())
    }

}