package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val TRACK_FROM_HISTORY = "historyTrack"
class SearchActivity : AppCompatActivity() {
    private val imdbBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(imdbBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ITunesApi::class.java)

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchHistory: SearchHistory
    private lateinit var savedText : String

    private var tracks = ArrayList<Track>()
    private var savedTracks = ArrayList<Track>()
    private val trackAdapter = TrackAdapter()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(TRACK_FROM_HISTORY, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        savedText = savedInstanceState?.getString(EDIT_TEXT) ?: ""

        if (savedText != "") {
            val json = savedInstanceState?.getString(SAVED_TRACKS)
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            savedTracks = Gson().fromJson(json, type) as ArrayList<Track>
            binding.youSearch.isVisible = false
            binding.cleanHistory.isVisible = false
            tracks = savedTracks
        }
        trackAdapter.tracks = tracks
        binding.tracksList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.tracksList.adapter = trackAdapter

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
            onStop()
        }

        binding.updateSearch.setOnClickListener {
            binding.placeholderSearchGroup.isVisible = false
            binding.updateSearch.isVisible = false
            requestToServer()
        }

        binding.cleanHistory.setOnClickListener {
            searchHistory.clearHistory()
            tracks.clear()
            binding.youSearch.isVisible = false
            binding.cleanHistory.isVisible = false
            trackAdapter.notifyDataSetChanged()
        }

        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (searchHistory.getList().isNotEmpty() && savedText == "") {
                binding.youSearch.isVisible = true
                binding.cleanHistory.isVisible = true
                tracks.addAll(searchHistory.getList())
            }
        }

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.inputEditText.text.isNotEmpty()) {
                    binding.placeholderSearchGroup.isVisible = false
                    binding.updateSearch.isVisible = false
                    binding.youSearch.isVisible = false
                    binding.cleanHistory.isVisible = false
                    binding.inputEditText.clearFocus()
                    tracks.clear()
                    requestToServer()
                }
            }
            false
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
            binding.placeholderSearchGroup.isVisible = false
            binding.updateSearch.isVisible = false
            trackAdapter.notifyDataSetChanged()
        }

        val simpleTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val checkVisibility = s?.isNotEmpty() == true
                if (s?.isEmpty() == true) tracks.clear()
                savedText = s.toString()
                binding.clearIcon.isVisible = checkVisibility
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
        outState.putString(EDIT_TEXT, savedText)
        val json = Gson().toJson(savedTracks)
        outState.putString(SAVED_TRACKS, json)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getString(EDIT_TEXT, savedText)
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
    private fun requestToServer() {
        itunesService.search(binding.inputEditText.text.toString()).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                val responseBody = response.body()?.results
                when (response.code()) {
                    200 -> {
                        tracks.clear()
                        if (responseBody?.isNotEmpty() == true) {
                            tracks.addAll(responseBody)
                            savedTracks = tracks
                            trackAdapter.notifyDataSetChanged()
                        } else
                            showPlaceholderNothingFound()
                    }
                    else -> showPlaceholderNetwork()
                }
            }
            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showPlaceholderNetwork()
            }
        })
    }
    companion object {
        private const val EDIT_TEXT = ""
        private const val SAVED_TRACKS = ""
        const val TRACK_INF = "trackInf"
    }
}