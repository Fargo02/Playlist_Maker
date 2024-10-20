package com.example.playlistmaker.di

import com.example.playlistmaker.ui.create_playlist.view_model.CreatePlaylistViewModel
import com.example.playlistmaker.ui.library.view_model.FavouriteTracksViewModel
import com.example.playlistmaker.ui.library.view_model.MediaLibraryViewModel
import com.example.playlistmaker.ui.library.view_model.PlaylistListViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.playlist.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel { params ->
        PlayerViewModel(get(), get(), get(), params.get())
    }

    viewModel {
        MediaLibraryViewModel()
    }

    viewModel {
        FavouriteTracksViewModel(get())
    }

    viewModel {
        PlaylistListViewModel(get())
    }

    viewModel { params ->
        CreatePlaylistViewModel(get(), params.get())
    }

    viewModel { params ->
        PlaylistViewModel(get(), get(), params.get())
    }
}