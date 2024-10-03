package com.example.playlistmaker.ui.search.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.fragment.PlayerFragment
import com.example.playlistmaker.ui.search.GetTrackListModel
import com.example.playlistmaker.ui.search.view_model.HistoryState
import com.example.playlistmaker.ui.search.view_model.SaveTracksState
import com.example.playlistmaker.ui.search.view_model.SearchState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.ui.TrackAdapter
import com.example.playlistmaker.utils.BindingFragment
import com.example.playlistmaker.utils.debounce
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment(): BindingFragment<FragmentSearchBinding>() {

    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var savedText : String

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private var tracks = ArrayList<Track>()

    private var trackAdapter: TrackAdapter? = null

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedText = ""

        onTrackClickDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            val json = Gson().toJson(track)
            findNavController().navigate(R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(json))
            viewModel.updateTrack(track)
            onDestroy()
        }

        trackAdapter = TrackAdapter { track ->
            val inputMethodManager =
                requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
            onTrackClickDebounce(track)
        }

        trackAdapter?.tracks = tracks
        binding.tracksList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.tracksList.adapter = trackAdapter

        viewModel.observeOnTrackClicked().observe(viewLifecycleOwner) {
            removeAndPutTrack(it)
        }

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeHistoryTrackListener().observe(viewLifecycleOwner) {
            historyRender(it)
        }

        viewModel.searchDebounce(savedText)

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
            trackAdapter?.notifyDataSetChanged()
        }

        binding.inputEditText.setOnFocusChangeListener { _, _ ->
            viewModel.getHistoryTracks( savedText)
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
            viewModel.getHistoryTracks(savedText)
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

                viewModel.searchDebounce(changedText = s?.toString() ?: "")

                viewModel.getHistoryTracks(savedText)
            }

            override fun afterTextChanged(s: Editable?) { }
        }
        simpleTextWatcher.let { binding.inputEditText.addTextChangedListener(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        trackAdapter = null
    }

    private fun removeAndPutTrack(track: Track) {
        tracks.remove(track)
        tracks.add(0, track)
        trackAdapter?.notifyDataSetChanged()
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
        trackAdapter?.tracks?.clear()
        trackAdapter?.tracks?.addAll(tracks)
        trackAdapter?.notifyDataSetChanged()
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

    private fun historyRender(state: HistoryState) {
        when (state) {
            is HistoryState.Content -> showContentHistory(state.tracks)
            is HistoryState.Empty -> showEmptyHistory()
        }
    }

    private fun showContentHistory(tracks: List<Track>) {
        binding.cleanHistory.isVisible = true
        binding.youSearch.isVisible = true
        binding.tracksList.isVisible = true
        trackAdapter?.tracks?.clear()
        trackAdapter?.tracks?.addAll(tracks)
        trackAdapter?.notifyDataSetChanged()
    }

    private fun showEmptyHistory() {
        binding.cleanHistory.isVisible = false
        binding.youSearch.isVisible = false
        binding.tracksList.isVisible = false
        trackAdapter?.tracks?.clear()
        trackAdapter?.notifyDataSetChanged()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }
}