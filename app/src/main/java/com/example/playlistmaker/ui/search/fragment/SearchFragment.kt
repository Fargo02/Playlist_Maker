package com.example.playlistmaker.ui.search.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.fragment.PlayerFragment
import com.example.playlistmaker.ui.search.GetTrackListModel
import com.example.playlistmaker.ui.search.view_model.SaveTracksState
import com.example.playlistmaker.ui.search.view_model.SearchState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.ui.TrackAdapter
import com.example.playlistmaker.utils.BindingFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment(): BindingFragment<FragmentSearchBinding>() {

    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var savedText : String

    private var isClickAllowed = true

    private var tracks = ArrayList<Track>()
    private var savedTracks = ArrayList<Track>()
    private val trackAdapter = TrackAdapter()

    private var mainThreadHandler: Handler? = null
    private var consumerRunnable: Runnable? = null

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainThreadHandler = Handler(Looper.getMainLooper())

        savedText = savedInstanceState?.getString(EMPTY_STRING) ?: ""

        Log.i("saveString", "$savedText")

        if (savedText != "") {
            val json = savedInstanceState?.getString(EMPTY_STRING)
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            savedTracks = Gson().fromJson(json, type) as ArrayList<Track>
            binding.youSearch.isVisible = false
            binding.cleanHistory.isVisible = false
            tracks = savedTracks
        }
        trackAdapter.tracks = tracks
        binding.tracksList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.tracksList.adapter = trackAdapter

        viewModel.observeOnTrackClicked().observe(viewLifecycleOwner) {
            removeAndPutTrack(it)
        }

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeGetTrackList().observe(viewLifecycleOwner) {
            updateTrackList(it)
        }

        clickDebounce().also {
            trackAdapter.setOnItemClickListener { track ->
                viewModel.updateTrack(track, tracks)
                val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
                val json = Gson().toJson(track)
                findNavController().navigate(
                    R.id.action_searchFragment_to_playerFragment,
                    PlayerFragment.createArgs(json)
                )
            }
        }

        binding.updateSearch.setOnClickListener {
            binding.placeholderSearchGroup.isVisible = false
            binding.updateSearch.isVisible = false
            viewModel.searchDebounce(savedText)
        }

        binding.cleanHistory.setOnClickListener {
            viewModel.clearHistory()
            tracks.clear()
            binding.youSearch.isVisible = false
            binding.cleanHistory.isVisible = false
            trackAdapter.notifyDataSetChanged()
        }

        binding.inputEditText.setOnFocusChangeListener { _, _ ->
            viewModel.getTrackFromSharedPreferences(true, savedText)
        }

        binding.clearIcon.setOnClickListener {
            val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
            binding.inputEditText.setText("")
            binding.inputEditText.clearFocus()
            tracks.clear()
            binding.youSearch.isVisible = false
            binding.cleanHistory.isVisible = false
            binding.placeholderSearchGroup.isVisible = false
            binding.updateSearch.isVisible = false
            viewModel.getTrackFromSharedPreferences(true, savedText)
        }

        val simpleTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val checkVisibility = s?.isNotEmpty() == true
                savedText = s.toString()
                binding.tracksList.isVisible = true
                binding.placeholderSearchGroup.isVisible = false
                binding.updateSearch.isVisible = false
                binding.youSearch.isVisible = false
                binding.cleanHistory.isVisible = false
                binding.clearIcon.isVisible = checkVisibility
                binding.progressBar.isVisible = checkVisibility

                tracks.clear()
                viewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )

                viewModel.getTrackFromSharedPreferences(!checkVisibility, savedText)
            }

            override fun afterTextChanged(s: Editable?) { }
        }
        simpleTextWatcher.let { binding.inputEditText.addTextChangedListener(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EMPTY_STRING, savedText)
        val json = Gson().toJson(savedTracks)
        outState.putString(EMPTY_STRING, json)
    }

    private fun updateTrackList(state: SaveTracksState){
        when (state) {
            is SaveTracksState.Content -> showSaveTracks(state.tracks)
            is SaveTracksState.Empty -> Log.i("empty", "empty")
        }

    }

    private fun showSaveTracks(trackListData: GetTrackListModel){
        binding.cleanHistory.isVisible = trackListData.isVisible
        binding.youSearch.isVisible = trackListData.isVisible
        tracks.clear()
        tracks.addAll(trackListData.trackList)
        trackAdapter.notifyDataSetChanged()
    }

    private fun removeAndPutTrack(track: Track) {
        tracks.remove(track)
        tracks.add(0, track)
        trackAdapter.notifyDataSetChanged()
    }

    private fun showLoading() {
        binding.tracksList.isVisible = false
        binding.placeholderSearchGroup.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun showContent(tracks: List<Track>) {
        binding.tracksList.isVisible = true
        binding.placeholderSearchGroup.isVisible = false
        binding.progressBar.isVisible = false
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun showError() {
        binding.tracksList.isVisible = false
        binding.placeholderSearchGroup.isVisible = true
        binding.progressBar.isVisible = false
        binding.updateSearch.isVisible = true
        binding.textPlaceholder.text = getString(R.string.problem_with_network)
        binding.imagePlaceholder.setImageResource(R.drawable.problem_with_network)
    }

    private fun showEmpty() {
        binding.tracksList.isVisible = false
        binding.placeholderSearchGroup.isVisible = true
        binding.progressBar.isVisible = false
        binding.textPlaceholder.text = getString(R.string.nothing_found)
        binding.imagePlaceholder.setImageResource(R.drawable.nothing_found)
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Error -> showError()
            is SearchState.Empty -> showEmpty()
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler?.postDelayed({ isClickAllowed = true },
                CLICK_DEBOUNCE_DELAY
            )
        }
        return current
    }

    override fun onDestroyView() {
        consumerRunnable?.let { mainThreadHandler?.removeCallbacks(it) }
        super.onDestroyView()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val EMPTY_STRING = ""
    }
}