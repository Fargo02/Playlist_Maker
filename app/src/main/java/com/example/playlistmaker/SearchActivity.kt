package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toolbar
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.roundToInt

const val TRACK_FROM_HISTORY = "historyTrack"
class SearchActivity : AppCompatActivity() {
    private lateinit var savedText : String

    private val imdbBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(imdbBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ITunesApi::class.java)

    private lateinit var placeholderMessage: LinearLayout
    private lateinit var updateButton: Button
    private lateinit var cleanHistoryButton: Button
    private lateinit var textPlaceholder: TextView
    private lateinit var youSearchText: TextView
    private lateinit var imagePlaceholder: ImageView
    private lateinit var inputEditText: EditText

    private lateinit var searchHistory: SearchHistory

    private var tracks = ArrayList<Track>()

    private val trackAdapter = TrackAdapter()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPreferences = getSharedPreferences(TRACK_FROM_HISTORY, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        val tracksList = findViewById<RecyclerView>(R.id.tracksList)
        trackAdapter.tracks = tracks
        tracksList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksList.adapter = trackAdapter

        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val searchBack = findViewById<Toolbar>(R.id.searchBack)

        inputEditText = findViewById(R.id.inputEditText)
        placeholderMessage = findViewById(R.id.placeholderSearch)
        updateButton = findViewById(R.id.updateSearch)
        cleanHistoryButton = findViewById(R.id.cleanHistory)
        textPlaceholder = findViewById(R.id.textPlaceholder)
        youSearchText = findViewById(R.id.youSearch)
        imagePlaceholder = findViewById(R.id.imagePlaceholder)


        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (searchHistory.getList().isNotEmpty()) {
                youSearchText.isVisible = true
                cleanHistoryButton.isVisible = true
                tracks.addAll(searchHistory.getList())
            }
        }

        updateButton.setOnClickListener {
            placeholderMessage.isVisible = false
            updateButton.isVisible = false
            requestToServer()
        }

        cleanHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            tracks.clear()
            youSearchText.isVisible = false
            cleanHistoryButton.isVisible = false
            trackAdapter.notifyDataSetChanged()
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    placeholderMessage.isVisible = false
                    updateButton.isVisible = false
                    youSearchText.isVisible = false
                    cleanHistoryButton.isVisible = false
                    requestToServer()
                }
            }
            false
        }

        searchBack.setNavigationOnClickListener {
            finish()
        }

        trackAdapter.setOnItemClickListener { track ->
            searchHistory.addTrack(track)
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            tracks.clear()
            tracks.addAll(searchHistory.getList())
            if (searchHistory.getList().isNotEmpty()) {
                youSearchText.isVisible = true
                cleanHistoryButton.isVisible = true
            }
            trackAdapter.notifyDataSetChanged()
        }

        val simpleTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val checkVisibility = s?.isNotEmpty() == true
                if (s?.isEmpty() == true) tracks.clear()
                savedText = s.toString()
                clearButton.isVisible = checkVisibility
                if (searchHistory.getList().isNotEmpty()) {
                    cleanHistoryButton.isVisible = checkVisibility
                    youSearchText.isVisible = checkVisibility
                }
            }

            override fun afterTextChanged(s: Editable?) { }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, savedText)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getString(EDIT_TEXT, savedText)
    }
    private fun showPlaceholderNothingFound() {
        placeholderMessage.isVisible = true
        textPlaceholder.text = getString(R.string.nothing_found)
        imagePlaceholder.setImageResource(R.drawable.nothing_found)
        tracks.clear()
        trackAdapter.notifyDataSetChanged()
    }
    private fun showPlaceholderNetwork() {
        placeholderMessage.isVisible = true
        updateButton.isVisible = true
        textPlaceholder.text = getString(R.string.problem_with_network)
        imagePlaceholder.setImageResource(R.drawable.problem_with_network)
        tracks.clear()
        trackAdapter.notifyDataSetChanged()
    }
    private fun requestToServer() {
        itunesService.search(inputEditText.text.toString()).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                val responseBody = response.body()?.results
                when (response.code()) {
                    200 -> {
                        tracks.clear()
                        if (responseBody?.isNotEmpty() == true) {
                            tracks.addAll(responseBody)
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
    }
}