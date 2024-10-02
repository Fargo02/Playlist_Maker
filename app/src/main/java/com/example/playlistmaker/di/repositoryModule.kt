package com.example.playlistmaker.di

import com.example.playlistmaker.data.db.converter.TrackDbConverter
import com.example.playlistmaker.data.favourites.FavouritesRepositoryImpl
import com.example.playlistmaker.data.player.PlayerRepositoryImpl
import com.example.playlistmaker.data.search.TracksRepositoryImpl
import com.example.playlistmaker.data.sharing.SharingHistoryTrackRepositoryImpl
import com.example.playlistmaker.domain.favourites.FavouritesRepository
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.sharing.history.SharingHistoryTrackRepository
import org.koin.dsl.module

val repositoryModule = module {

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    single<SharingHistoryTrackRepository> {
        SharingHistoryTrackRepositoryImpl(get(), get())
    }

    single <FavouritesRepository>{
        FavouritesRepositoryImpl(get(), get())
    }

    factory { TrackDbConverter() }
}