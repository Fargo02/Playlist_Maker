package com.example.playlistmaker.ui.library.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistListBinding
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.ui.library.ui.PlaylistAdapter
import com.example.playlistmaker.ui.library.view_model.PlaylistListViewModel
import com.example.playlistmaker.ui.player.fragment.PlayerFragment
import com.example.playlistmaker.ui.playlist.fragment.PlaylistFragment
import com.example.playlistmaker.utils.BindingFragment
import com.example.playlistmaker.utils.ScreenState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistListFragment(): BindingFragment<FragmentPlaylistListBinding>() {

    private val viewModel: PlaylistListViewModel by viewModel()
    private var playlists = ArrayList<Playlist>()
    private var playlistAdapter: PlaylistAdapter? = null

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPlaylistListBinding {
        return FragmentPlaylistListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fillData()

        playlistAdapter = PlaylistAdapter { playlist ->
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_playlistFragment,
                PlaylistFragment.createArgs(playlist.id))
        }

        viewModel.observerStateLiveData().observe(viewLifecycleOwner) {
            render(it)
        }

        playlistAdapter?.playlists = playlists
        binding.playlists.layoutManager = GridLayoutManager(requireContext(),2)
        binding.playlists.adapter = playlistAdapter

        binding.placeholderGroup.isVisible = true

        binding.createNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_fragmentCreatePlaylist)
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.placeholderGroup.isVisible = false
        binding.playlists.isVisible = true
        playlistAdapter?.playlists?.clear()
        playlistAdapter?.playlists?.addAll(playlists)
        playlistAdapter?.notifyDataSetChanged()
    }

    private fun showEmpty() {
        binding.placeholderGroup.isVisible = true
        binding.playlists.isVisible = false
        playlistAdapter?.playlists?.clear()
        playlistAdapter?.notifyDataSetChanged()
    }

    private fun render(state: ScreenState<out List<Playlist>>) {
        when (state) {
            is ScreenState.Content -> showContent(state.data)
            is ScreenState.Empty -> showEmpty()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        playlistAdapter = null
    }

    companion object {
        fun newInstance() = PlaylistListFragment()
    }

}