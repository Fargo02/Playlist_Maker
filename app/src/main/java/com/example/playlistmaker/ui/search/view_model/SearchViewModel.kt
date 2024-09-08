package com.example.playlistmaker.ui.search.view_model


import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.sharing.history.SharingHistoryTrackInteractor
import com.example.playlistmaker.ui.search.GetTrackListModel
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val sharedInteractor : SharingHistoryTrackInteractor,
    private val searchInteractor: TracksInteractor
): ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var latestSearchText: String? = null

    private val trackSearchDebounce = debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, false) {
        latestSearchText?.let { requestToServer(it) }
    }

    private val getTrackList = MutableLiveData<SaveTracksState>()
    fun observeGetTrackList(): LiveData<SaveTracksState> = getTrackList

    private val onTrackClicked = MutableLiveData<Track>()
    fun observeOnTrackClicked(): LiveData<Track> = onTrackClicked

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState() : LiveData<SearchState> = stateLiveData

    fun getTrackFromSharedPreferences(isVisibility: Boolean, savedText: String){
        if (sharedInteractor.getList().isNotEmpty() && savedText == "") {
            getTrackList.postValue(SaveTracksState.Content(GetTrackListModel(sharedInteractor.getList(), isVisibility)))
        } else {
            getTrackList.postValue(SaveTracksState.Empty)
        }
    }

    fun updateTrack(track: Track, tracksList: List<Track>) {
        if (sharedInteractor.getList() == tracksList) {
            onTrackClicked.postValue(track)
            sharedInteractor.addTrack(track)
        } else {
            sharedInteractor.addTrack(track)
        }
    }

    fun clearHistory() {
        sharedInteractor.clearHistory()
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            trackSearchDebounce(changedText)
        }
    }

    private fun requestToServer(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            viewModelScope.launch {
                searchInteractor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()

        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            errorMessage != null -> {
                renderState(
                    SearchState.Error
                )
            }
            tracks.isEmpty() -> {
                SearchState.Empty
            }
            else -> {
                renderState(
                    SearchState.Content(
                        tracks = tracks,
                    )
                )
            }
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }
}