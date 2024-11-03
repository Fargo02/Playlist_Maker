package com.example.playlistmaker.ui.player.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.player.PlayerState
import com.example.playlistmaker.domain.player.PlayerState.DEFAULT
import com.example.playlistmaker.domain.player.PlayerState.PAUSED
import com.example.playlistmaker.domain.player.PlayerState.PLAYING
import com.example.playlistmaker.domain.player.PlayerState.PREPARED
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.mapper.ArtworkMapper
import com.example.playlistmaker.ui.mapper.TimeMapper
import com.example.playlistmaker.ui.player.ui.PlayerPlaylistAdapter
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.utils.BindingFragment
import com.example.playlistmaker.utils.ScreenState
import com.example.playlistmaker.utils.debounce
import com.example.playlistmaker.utils.showSnackbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment(): BindingFragment<FragmentPlayerBinding>() {

    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit
    private lateinit var currentTrack: Track
    private lateinit var playerState: PlayerState
    private val trackTimeMapper: TimeMapper = TimeMapper()
    private var playlistAdapter: PlayerPlaylistAdapter? = null
    private var playlists = ArrayList<Playlist>()

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(currentTrack)
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val analytics = FirebaseAnalytics.getInstance(requireContext())

        val track = requireArguments().getString(CURRENT_TRACK) ?: ""
        val type = object : TypeToken<Track>() {}.type
        currentTrack = Gson().fromJson(track, type) as Track

        val bottomSheetContainer = binding.playlistsBottomSheet
        val overlay = binding.overlay

        val bottomSheetBehavior = bottomSheetContainer?.let {
            BottomSheetBehavior.from(it).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        binding.buttonPlay.isEnabled = false

        onPlaylistClickDebounce = debounce<Playlist>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { playlist ->
            if(playlist.trackList.contains(currentTrack.trackId.toString())) {
                showSnackbar(requireView(), requireContext(), playlist.name, R.string.alredy_add)
            } else {
                viewModel.insertTrack(currentTrack, currentTrack.trackId.toString(), playlist.id)
                showSnackbar(requireView(), requireContext(), playlist.name, R.string.added_to_playlist)
                viewModel.getPlaylist()
            }
        }

        playlistAdapter = PlayerPlaylistAdapter { playlist ->
            onPlaylistClickDebounce(playlist)
        }

        playlistAdapter?.playlists = playlists
        binding.playlists?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.playlists?.adapter = playlistAdapter

        bottomSheetBehavior?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay?.isVisible = false
                    }
                    else -> {
                        overlay?.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) { overlay?.alpha = slideOffset }
        })

        Glide.with(binding.cover)
            .load(ArtworkMapper.getCoverArtwork(currentTrack.artworkUrl100))
            .transform(
                CenterCrop(),
                RoundedCorners(resources.getDimensionPixelSize(R.dimen.mark_8dp))
            )
            .placeholder(R.drawable.big_placeholder)
            .into(binding.cover)

        viewModel.observePlayerStateListener().observe(viewLifecycleOwner) { state ->
            when (state!!) {
                PREPARED -> {
                    playerState = PAUSED
                    binding.buttonPlay.isEnabled = true
                    binding.playingTime.text = resources.getString(R.string.start_track)
                    viewModel.updateCurrentTime()
                    binding.buttonPlay.setBackgroundResource(R.drawable.button_play_on)
                }
                PLAYING -> {
                    playerState = PAUSED
                    binding.buttonPlay.isEnabled = true
                    viewModel.updateCurrentTime()
                    binding.buttonPlay.setBackgroundResource(R.drawable.button_play_on)
                }
                PAUSED -> {
                    playerState = PLAYING
                    binding.buttonPlay.isEnabled = true
                    binding.buttonPlay.setBackgroundResource(R.drawable.button_play_off)
                }
                DEFAULT -> {
                    playerState = PREPARED
                    binding.buttonPlay.isEnabled = true
                    binding.buttonPlay.setBackgroundResource(R.drawable.button_play_off)
                    binding.playingTime.text = resources.getString(R.string.start_track)
                }
            }
        }

        binding.buttonPlay.setOnClickListener {
            viewModel.listener.onChange(playerState)
        }

        binding.buttonBack.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.trackName.text = currentTrack.trackName
        binding.artistName.text = currentTrack.artistName
        binding.playingTime.text = resources.getString(R.string.start_track)
        binding.trackTime.text = trackTimeMapper.getString(currentTrack.trackTimeMillis)
        binding.trackYear.text = currentTrack.releaseDate
        binding.trackGenre.text = currentTrack.primaryGenreName
        binding.trackCountry.text = currentTrack.country

        if (currentTrack.collectionName == null) {
            binding.albumGroup!!.isVisible = false
        } else {
            binding.albumName.text = currentTrack.collectionName
        }

        binding.buttonAddToList.setImageResource(R.drawable.ic_add_to_list_off)

        binding.buttonAddToList.setOnClickListener {
            viewModel.getPlaylist()
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        viewModel.observeStateFavourite().observe(viewLifecycleOwner) {
            binding.buttonLike.setImageResource(
                if (it) R.drawable.ic_like_on else R.drawable.ic_like_off
            )
        }

        viewModel.observeFavouriteState().observe(viewLifecycleOwner) { isFavourite ->
            currentTrack.isFavorite = isFavourite
            binding.buttonLike.setImageResource(
                if (isFavourite) R.drawable.ic_like_on else R.drawable.ic_like_off
            )
        }

        viewModel.observeCurrentTimeListener().observe(viewLifecycleOwner) { time ->
            binding.playingTime.text = time
        }

        viewModel.observePlaylistStateListener().observe(viewLifecycleOwner) {
            renderPlaylist(it)
        }

        binding.buttonLike.setOnClickListener {
            analytics.logEvent("Add_favourite_track") {
                param("Name", currentTrack.trackName)
            }
            viewModel.onFavoriteClicked(currentTrack)
        }

        binding.createNewPlaylist?.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_fragmentCreatePlaylist)
        }
    }

    override fun onPause() {
        super.onPause()
        playerState = PAUSED
        viewModel.listener.onChange(playerState)
    }

    private fun renderPlaylist(state: ScreenState<out List<Playlist>>) {
        when (state) {
            is ScreenState.Empty -> showEmpty()
            is ScreenState.Content -> showContent(state.data)
        }
    }

    private fun showContent(playlists: List<Playlist>){
        playlistAdapter?.playlists?.clear()
        playlistAdapter?.playlists?.addAll(playlists)
        playlistAdapter?.notifyDataSetChanged()
    }
    private fun showEmpty() { }

    companion object{

        private const val CLICK_DEBOUNCE_DELAY = 200L
        private const val CURRENT_TRACK = "current_track"

        fun createArgs(track: String): Bundle =
            bundleOf(
            CURRENT_TRACK to track
        )
    }

}