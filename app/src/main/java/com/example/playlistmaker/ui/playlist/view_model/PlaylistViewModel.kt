package com.example.playlistmaker.ui.playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.sharing.settings.SharingInteractor
import com.example.playlistmaker.ui.converter.PlaylistConverter
import com.example.playlistmaker.ui.mapper.splitStringToLongMapper
import com.example.playlistmaker.ui.playlist.model.CurrentPlaylist
import com.example.playlistmaker.utils.ScreenState
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val sharingSettingsInteractor: SharingInteractor,
    private val playlistId: Long
): ViewModel() {

    private val converter = PlaylistConverter()

    private lateinit var currentPlaylist: Playlist

    private val playlistListener = MutableLiveData<CurrentPlaylist>()
    fun observePlaylistListener(): LiveData<CurrentPlaylist> = playlistListener

    private val tracksStateListener = MutableLiveData<ScreenState<out List<Track>>>()
    fun observeTracksStateListener(): LiveData<ScreenState<out List<Track>>> = tracksStateListener

    fun getPlaylistInf() {
        viewModelScope.launch {
            var durationInMills = 0L
            currentPlaylist = playlistInteractor.getPlaylist(playlistId)
            val trackList = if (currentPlaylist.trackList != "") {
                splitStringToLongMapper(currentPlaylist.trackList).map { id ->
                    val track = playlistInteractor.getTrack(id)
                    durationInMills += track.trackTimeMillis.toLong()
                    track
                }
            } else {
                emptyList()
            }

            playlistListener.postValue(converter.map(currentPlaylist, durationInMills))

            processResult(trackList.reversed())
        }

    }

    fun deleteTrack(idTrack: Long, idPlaylist: Long) {
        viewModelScope.launch {
            playlistInteractor.deleteTrack(idTrack, idPlaylist)
            getPlaylistInf()

        }
    }

    fun deletePlaylist(idPlaylist: Long) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(idPlaylist)
        }
    }

    fun getSharePlaylist(message: String) {
        sharingSettingsInteractor.share(message)
    }

    private fun processResult(foundPlaylist: List<Track>?) {
        renderState(
            if (foundPlaylist.isNullOrEmpty()) ScreenState.Empty
            else ScreenState.Content(foundPlaylist)
        )
    }

    private fun renderState(state: ScreenState<out List<Track>>) {
        tracksStateListener.postValue(state)
    }

}