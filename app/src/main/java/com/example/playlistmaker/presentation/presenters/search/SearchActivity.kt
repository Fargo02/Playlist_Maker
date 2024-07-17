package com.example.playlistmaker.presentation.presenters.search

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.presentation.presenters.player.PlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.SearchHistory
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.TrackAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val TRACK_FROM_HISTORY = "historyTrack"
class SearchActivity : AppCompatActivity() {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 1200L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val EMPTY_STRING = ""
        const val TRACK_INF = "trackInf"
    }

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchHistory: SearchHistory
    private lateinit var savedText : String

    private var isClickAllowed = true
    private var tracks = ArrayList<Track>()
    private var savedTracks = ArrayList<Track>()
    private val trackAdapter = TrackAdapter()
    private var mainThreadHandler: Handler? = null
    private var consumerRunnable: Runnable? = null
    private val searchRunnable = Runnable { requestToServer() }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainThreadHandler = Handler(Looper.getMainLooper())

        val sharedPreferences = getSharedPreferences(TRACK_FROM_HISTORY, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        savedText = savedInstanceState?.getString(EMPTY_STRING) ?: ""

        if (savedText != "") {
            val json = savedInstanceState?.getString(EMPTY_STRING)
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            savedTracks = Gson().fromJson(json, type) as ArrayList<Track>
            binding.youSearch.isVisible = false
            binding.cleanHistory.isVisible = false
            tracks = savedTracks
        }
        trackAdapter.tracks = tracks
        binding.tracksList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.tracksList.adapter = trackAdapter

        clickDebounce().also {
            trackAdapter.setOnItemClickListener { track ->
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
                if (searchHistory.getList() == tracks) {
                    val position = tracks.indexOf(track)
                    tracks.remove(track)
                    tracks.add(0, track)
                    trackAdapter.notifyItemRemoved(position)
                    trackAdapter.notifyItemInserted(0)
                    trackAdapter.notifyItemRangeChanged(0, tracks.size)
                    searchHistory.addTrack(track)
                } else {
                    searchHistory.addTrack(track)
                }
                val json = Gson().toJson(track)
                val playerIntent = Intent(this, PlayerActivity::class.java)
                playerIntent.putExtra(TRACK_INF, json)
                startActivity(playerIntent)
            }
        }

        binding.updateSearch.setOnClickListener {
            binding.placeholderSearchGroup.isVisible = false
            binding.updateSearch.isVisible = false
            searchDebounce()
        }

        binding.cleanHistory.setOnClickListener {
            searchHistory.clearHistory()
            tracks.clear()
            binding.youSearch.isVisible = false
            binding.cleanHistory.isVisible = false
            trackAdapter.notifyDataSetChanged()
        }

        binding.inputEditText.setOnFocusChangeListener { _, _ ->
            if ((searchHistory.getList().isNotEmpty() && savedText == "")) {
                binding.youSearch.isVisible = true
                binding.cleanHistory.isVisible = true
                tracks.addAll(searchHistory.getList())
                trackAdapter.notifyDataSetChanged()
            }
        }

        binding.searchBack.setNavigationOnClickListener {
            Log.d("Track", "$tracks")
            finish()
        }

        binding.clearIcon.setOnClickListener {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
            binding.inputEditText.setText("")
            binding.inputEditText.clearFocus()
            tracks.clear()
            binding.youSearch.isVisible = false
            binding.cleanHistory.isVisible = false
            binding.placeholderSearchGroup.isVisible = false
            binding.updateSearch.isVisible = false
            trackAdapter.notifyDataSetChanged()
        }

        val simpleTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val checkVisibility = s?.isNotEmpty() == true
                savedText = s.toString()
                binding.placeholderSearchGroup.isVisible = false
                binding.updateSearch.isVisible = false
                binding.youSearch.isVisible = false
                binding.cleanHistory.isVisible = false
                binding.clearIcon.isVisible = checkVisibility
                binding.progressBar.isVisible = checkVisibility
                if (!checkVisibility) {
                    tracks.clear()
                } else {
                    tracks.clear()
                    searchDebounce()
                }
                if (searchHistory.getList().isNotEmpty() && savedText == "") {
                    binding.cleanHistory.isVisible = checkVisibility
                    binding.youSearch.isVisible = checkVisibility
                }
            }

            override fun afterTextChanged(s: Editable?) { }
        }
        binding.inputEditText.addTextChangedListener(simpleTextWatcher)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EMPTY_STRING, savedText)
        val json = Gson().toJson(savedTracks)
        outState.putString(EMPTY_STRING, json)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getString(EMPTY_STRING, savedText)
    }
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler?.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
    private fun searchDebounce() {
        mainThreadHandler?.removeCallbacks(searchRunnable)
        mainThreadHandler?.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    private fun showPlaceholderNothingFound() {
        binding.placeholderSearchGroup.isVisible = true
        binding.textPlaceholder.text = getString(R.string.nothing_found)
        binding.imagePlaceholder.setImageResource(R.drawable.nothing_found)
    }
    private fun showPlaceholderNetwork() {
        binding.placeholderSearchGroup.isVisible = true
        binding.updateSearch.isVisible = true
        binding.textPlaceholder.text = getString(R.string.problem_with_network)
        binding.imagePlaceholder.setImageResource(R.drawable.problem_with_network)
    }

    override fun onDestroy() {
        consumerRunnable?.let { mainThreadHandler?.removeCallbacks(it) }
        super.onDestroy()
    }
    private fun requestToServer() {
        Creator.provideTracksInteractor().searchTracks(
            binding.inputEditText.text.toString(),
            object : TracksInteractor.TracksConsumer {
                override fun consume(foundMovies: List<Track>) {
                    consumerRunnable?.let { mainThreadHandler?.removeCallbacks(it) }
                    val runnable = Runnable {
                        binding.progressBar.isVisible = false
                        if (foundMovies.isNotEmpty()) {
                            tracks.addAll(foundMovies)
                            savedTracks = tracks
                            trackAdapter.notifyDataSetChanged()
                        } else {
                            showPlaceholderNothingFound()
                        }
                    }
                    consumerRunnable = runnable
                    mainThreadHandler?.post(runnable)
                }
            }
        )
    }
}