package com.example.playlistmaker.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.player.PlayerRepositoryImpl
import com.example.playlistmaker.data.sharing.SharingHistoryTrackImpl
import com.example.playlistmaker.data.search.TracksRepositoryImpl
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.sharing.ExternalNavigatorImpl
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.sharing.impl.SharingHistoryTrackInteractorImpl
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.sharing.history.SharingHistoryTrackInteractor
import com.example.playlistmaker.domain.sharing.history.SharingHistoryTrackRepository
import com.example.playlistmaker.domain.sharing.impl.SharingSettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.settings.ExternalNavigator
import com.example.playlistmaker.domain.sharing.settings.SharingSettingsInteractor

object Creator {

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }
    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }
    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }

    private fun getSharedRepository(sharedPreferences : SharedPreferences): SharingHistoryTrackRepository {
        return SharingHistoryTrackImpl(sharedPreferences)
    }
    fun provideSharedInteractor(sharedPreferences : SharedPreferences): SharingHistoryTrackInteractor {
        return SharingHistoryTrackInteractorImpl(getSharedRepository(sharedPreferences))
    }

    private fun getSettingsRepository(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }
    fun provideSharingSettingsInteractor(context: Context): SharingSettingsInteractor {
        return SharingSettingsInteractorImpl(context, getSettingsRepository(context))
    }
}