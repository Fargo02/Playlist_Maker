package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private lateinit var savedText : String

    private val imdbBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(imdbBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ITunesApi::class.java)

    private lateinit var placeholderMessage: LinearLayout
    private  lateinit var updateButton: Button
    private lateinit var textPlaceholder: TextView
    private lateinit var imagePlaceholder: ImageView

    private val tracks = ArrayList<Track>()
    private val trackAdapter = TrackAdapter()

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val tracksList = findViewById<RecyclerView>(R.id.tracksList)

        trackAdapter.tracks = tracks
        tracksList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksList.adapter = trackAdapter


        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val searchBack = findViewById<Toolbar>(R.id.searchBack)

        placeholderMessage = findViewById(R.id.placeholderSearch)
        updateButton = findViewById(R.id.updateSearch)
        textPlaceholder = findViewById(R.id.textPlaceholder)
        imagePlaceholder = findViewById(R.id.imagePlaceholder)

        updateButton.setOnClickListener{
            placeholderMessage.visibility = View.GONE
            updateButton.visibility = View.GONE
            itunesService.search(savedText).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                    when (response.code()) {
                        200 -> {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results!!)
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

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    placeholderMessage.visibility = View.GONE
                    updateButton.visibility = View.GONE
                    itunesService.search(inputEditText.text.toString()).enqueue(object : Callback<TrackResponse> {
                        override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                            when (response.code()) {
                                200 -> {
                                    tracks.clear()
                                    if (response.body()?.results?.isNotEmpty() == true) {
                                        tracks.addAll(response.body()?.results!!)
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
            }
            false
        }

        searchBack.setNavigationOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
        }

        val simpleTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                savedText = s.toString()
                clearButton.isVisible = s?.isNotEmpty() == true
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
        placeholderMessage.visibility = View.VISIBLE
        textPlaceholder.text = getString(R.string.nothing_found)
        imagePlaceholder.setImageResource(R.drawable.nothing_found)
        tracks.clear()
        trackAdapter.notifyDataSetChanged()
    }
    private fun showPlaceholderNetwork() {
        placeholderMessage.visibility = View.VISIBLE
        updateButton.visibility = View.VISIBLE
        textPlaceholder.text = getString(R.string.problem_with_network)
        imagePlaceholder.setImageResource(R.drawable.problem_with_network)
        tracks.clear()
        trackAdapter.notifyDataSetChanged()
    }
    companion object {
        private const val EDIT_TEXT = ""
    }
}