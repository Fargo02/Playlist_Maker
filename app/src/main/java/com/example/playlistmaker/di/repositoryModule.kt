package com.example.playlistmaker.di

import com.example.playlistmaker.data.db.converter.HistoryTrackDbConverter
import com.example.playlistmaker.data.db.converter.PlaylistDbConverter
import com.example.playlistmaker.data.db.converter.TrackDbConverter
import com.example.playlistmaker.data.favourites.FavouritesRepositoryImpl
import com.example.playlistmaker.data.player.PlayerRepositoryImpl
import com.example.playlistmaker.data.search.TracksRepositoryImpl
import com.example.playlistmaker.data.history.HistoryTrackRepositoryImpl
import com.example.playlistmaker.data.playlist.PlaylistRepositoryImpl
import com.example.playlistmaker.domain.favourites.FavouritesRepository
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.history.HistoryTrackRepository
import com.example.playlistmaker.domain.playlist.PlaylistRepository
import org.koin.dsl.module

val repositoryModule = module {

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    single<HistoryTrackRepository> {
        HistoryTrackRepositoryImpl(get(), get())
    }

    single <FavouritesRepository> {
        FavouritesRepositoryImpl(get(), get())
    }

    single <PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get())
    }

    factory { TrackDbConverter() }

    factory { HistoryTrackDbConverter() }

    factory { PlaylistDbConverter() }

}