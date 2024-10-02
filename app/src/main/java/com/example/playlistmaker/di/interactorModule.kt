package com.example.playlistmaker.di

import com.example.playlistmaker.domain.favourites.FavouritesInteractor
import com.example.playlistmaker.domain.favourites.impl.FavouritesInteractorImpl
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.sharing.history.SharingHistoryTrackInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingHistoryTrackInteractorImpl
import com.example.playlistmaker.domain.sharing.impl.SharingSettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.settings.SharingSettingsInteractor
import org.koin.dsl.module

val interactorModule = module {

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<SharingHistoryTrackInteractor> {
        SharingHistoryTrackInteractorImpl(get())
    }

    single<SharingSettingsInteractor> {
        SharingSettingsInteractorImpl(get())
    }

    single<FavouritesInteractor> {
        FavouritesInteractorImpl(get())
    }
}