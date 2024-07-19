package com.example.playlistmaker.ui.search.view_model


import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.sharing.history.SharingHistoryTrackInteractor
import com.example.playlistmaker.ui.search.GetTrackListModel
import com.example.playlistmaker.utils.Creator

class SearchViewModel(
    private val sharedInteractor : SharingHistoryTrackInteractor,
    private val searchInteractor: TracksInteractor
): ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun factory(sharedPreferences : SharedPreferences, context : Context) = viewModelFactory {
            initializer {
                SearchViewModel(
                    Creator.provideSharedInteractor(sharedPreferences),
                    Creator.provideTracksInteractor(context)
                )
            }
        }
    }

    private val getTrackList = MutableLiveData<GetTrackListModel>()
    fun observeGetTrackList(): LiveData<GetTrackListModel> = getTrackList

    private val onTrackClicked = MutableLiveData<Track>()
    fun observeOnTrackClicked(): LiveData<Track> = onTrackClicked

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState() : LiveData<SearchState> = stateLiveData

    fun getTrackFromSharedPreferences(isVisibility: Boolean, savedText: String){
        if (sharedInteractor.getList().isNotEmpty() && savedText == "") {
            getTrackList.postValue(GetTrackListModel(sharedInteractor.getList(), isVisibility))
        } else {
            Log.i("ErrorGetTrack", "Ошибка")
        }
    }

    private val handler = Handler(Looper.getMainLooper())

    private var latestSearchText: String? = null

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

        if (latestSearchText == changedText || changedText == "") {
            handler.removeCallbacksAndMessages(null)
            return
        }

        this.latestSearchText = changedText

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { requestToServer(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        super.onCleared()
    }

    private fun requestToServer(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

                searchInteractor.searchTracks(
                newSearchText,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
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
                }
            )
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }
}