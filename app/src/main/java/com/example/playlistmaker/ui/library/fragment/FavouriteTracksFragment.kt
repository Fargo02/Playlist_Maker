package com.example.playlistmaker.ui.library.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavouritesBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.library.view_model.FavouriteState
import com.example.playlistmaker.ui.library.view_model.FavouriteTracksViewModel
import com.example.playlistmaker.ui.player.fragment.PlayerFragment
import com.example.playlistmaker.ui.ui.TrackAdapter
import com.example.playlistmaker.utils.BindingFragment
import com.example.playlistmaker.utils.debounce
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriteTracksFragment(): BindingFragment<FragmentFavouritesBinding>() {

    private var tracks = ArrayList<Track>()

    private var trackAdapter: TrackAdapter? = null

    private val viewModel: FavouriteTracksViewModel by viewModel()

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFavouritesBinding {
        return FragmentFavouritesBinding.inflate(inflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fillData()

        val onTrackClickDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            val json = Gson().toJson(track)
            findNavController().navigate(
                R.id.action_mediaLibraryFragment_to_playerFragment,
                PlayerFragment.createArgs(json))
        }

        trackAdapter = TrackAdapter { track -> onTrackClickDebounce(track) }

        trackAdapter?.tracks = tracks
        binding.tracksList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.tracksList.adapter = trackAdapter


        viewModel.observeFavouriteState().observe(viewLifecycleOwner) {
            render(it)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        trackAdapter = null
    }

    private fun render(state: FavouriteState) {
        when (state) {
            is FavouriteState.Empty -> showEmpty()
            is FavouriteState.Content -> showContent(state.tracks)
        }
    }
    private fun showContent(tracks: List<Track>){
        binding.placeholderGroup.isVisible = false
        binding.tracksList.isVisible = true
        trackAdapter?.tracks?.clear()
        trackAdapter?.tracks?.addAll(tracks)
        trackAdapter?.notifyDataSetChanged()
    }
    private fun showEmpty() {
        binding.placeholderGroup.isVisible = true
        binding.tracksList.isVisible = false
        trackAdapter?.tracks?.clear()
        trackAdapter?.notifyDataSetChanged()
    }

    companion object {

        fun newInstance() = FavouriteTracksFragment()
        private const val CLICK_DEBOUNCE_DELAY = 300L

    }

}