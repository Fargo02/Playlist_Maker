package com.example.playlistmaker.ui.search.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.history.HistoryTrackInteractor
import com.example.playlistmaker.utils.ScreenState
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val historyTrackInteractor : HistoryTrackInteractor,
    private val searchInteractor: TracksInteractor,
): ViewModel() {

    private var latestSearchText: String? = null

    private val trackSearchDebounce = debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, false) {
        latestSearchText?.let { requestToServer(it) }
    }

    private val onTrackClicked = MutableLiveData<Track>()
    fun observeOnTrackClicked(): LiveData<Track> = onTrackClicked

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState() : LiveData<SearchState> = stateLiveData

    private val historyTrackListener = MutableLiveData<ScreenState<out List<Track>>>()
    fun observeHistoryTrackListener() : LiveData<ScreenState<out List<Track>>> = historyTrackListener


    fun getHistoryTracks(savedText: String) {
        if (savedText == "") {
            viewModelScope.launch {
                historyTrackInteractor
                    .getHistoryTracks()
                    .collect {
                        processHistoryResult(it)
                    }
            }
        } else {
            renderHistoryState(ScreenState.Empty)
        }
    }

    fun updateTrack(track: Track) {
        viewModelScope.launch {
            historyTrackInteractor.insertTrack(track)
            renderHistoryState(
                ScreenState.Empty
            )
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyTrackInteractor.deleteAllTracks()
        }
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

    private fun processHistoryResult(foundTracks: List<Track>?) {
        renderHistoryState(
            if (foundTracks.isNullOrEmpty()) ScreenState.Empty
            else ScreenState.Content(foundTracks)
        )
    }

    private fun renderHistoryState(state: ScreenState<out List<Track>>) {
        historyTrackListener.postValue(state)
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
                renderState(
                    SearchState.Empty
                )
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

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}