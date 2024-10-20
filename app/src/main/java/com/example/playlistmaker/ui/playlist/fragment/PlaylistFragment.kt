package com.example.playlistmaker.ui.playlist.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.create_playlist.frgament.EditePlaylistFragment
import com.example.playlistmaker.ui.player.fragment.PlayerFragment
import com.example.playlistmaker.ui.playlist.model.CurrentPlaylist
import com.example.playlistmaker.ui.playlist.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.ui.TrackAdapter
import com.example.playlistmaker.utils.BindingFragment
import com.example.playlistmaker.utils.ScreenState
import com.example.playlistmaker.utils.debounce
import com.example.playlistmaker.utils.showSnackbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class PlaylistFragment : BindingFragment<FragmentPlaylistBinding>() {

    private lateinit var currentPlaylist : CurrentPlaylist

    private var playlistId: Long = 0

    private var tracks = ArrayList<Track>()

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private var trackAdapter: TrackAdapter? = null

    private val viewModel: PlaylistViewModel by viewModel {
        parametersOf(playlistId)
    }

    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(layoutInflater,container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistId = requireArguments().getLong(CURRENT_PLAYLIST)

        viewModel.getPlaylistInf()

        val overlay = binding.overlay

        onTrackClickDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            val json = Gson().toJson(track)
            findNavController().navigate(R.id.action_playlistFragment_to_playerFragment,
                PlayerFragment.createArgs(json))
            onDestroy()
        }

        trackAdapter = TrackAdapter(object : TrackAdapter.TrackClickListener{
            override fun onTrackClick(track: Track) {
                onTrackClickDebounce(track)
            }

            override fun onLongClickListener(track: Track) {
                showDialogDeleteTrack(track.trackId, currentPlaylist.id)
            }

        })

        trackAdapter?.tracks = tracks
        binding.tracksList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.tracksList.adapter = trackAdapter

        viewModel.observePlaylistListener().observe(viewLifecycleOwner) {

            currentPlaylist = it

            Glide.with(binding.cover)
                .load(it.imageUri)
                .transform(CenterCrop())
                .placeholder(R.drawable.big_placeholder)
                .into(binding.cover)

            if (it.description != "") binding.description.text = it.description
            else binding.description.isVisible = false

            binding.name.text = it.name
            binding.countTrack.text = it.count
            binding.duration.text = it.duration

        }

        viewModel.observeTracksStateListener().observe(viewLifecycleOwner) {
            tracksRender(it)
        }

        binding.buttonBack.setNavigationOnClickListener {
            findNavController().popBackStack()
        }


        initializePlaylistsBottomSheet()

        val bottomSheetContainerMore = binding.moreBottomSheet
        val bottomSheetBehaviorMore = bottomSheetContainerMore.let {
            BottomSheetBehavior.from(it).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        binding.playlistsBottomSheet.setOnClickListener{ }
        binding.moreBottomSheet.setOnClickListener{ }

        bottomSheetBehaviorMore.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.isVisible = false
                    }
                    else -> {
                        overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) { overlay.alpha = slideOffset }
        })


        binding.moreButton.setOnClickListener {
            bottomSheetBehaviorMore.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.playlistItem.name.text = currentPlaylist.name
            binding.playlistItem.count.text = currentPlaylist.count
            Glide.with(binding.playlistItem.cover)
                .load(currentPlaylist.imageUri)
                .transform(
                    CenterCrop(),
                    RoundedCorners(resources.getDimensionPixelSize(R.dimen.mark_2dp))
                )
                .placeholder(R.drawable.track_placeholder)
                .into(binding.playlistItem.cover)
        }

        binding.shareButton.setOnClickListener {
            if (tracks.isEmpty()) {
                showSnackbar(requireView(), requireContext(), resourceId = R.string.empty_playlist)
            } else {
                viewModel.getSharePlaylist(buildMessage())
            }
        }

        binding.sharingTextView.setOnClickListener {
            if (tracks.isEmpty()) {
                showSnackbar(requireView(), requireContext(), resourceId = R.string.empty_playlist)
            } else {
                viewModel.getSharePlaylist(buildMessage())
            }
        }

        binding.editeTextView.setOnClickListener {
            findNavController().navigate(R.id.action_playlistFragment_to_editePlaylistFragment,
                EditePlaylistFragment.createArgs(currentPlaylist.id))
            onDestroy()
        }

        binding.deleteTextView.setOnClickListener {
            showDialogDeletePlaylist()
        }
    }

    private fun initializePlaylistsBottomSheet() {
        val bottomSheet = binding.playlistsBottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        binding.root.post {
            bottomSheetBehavior.peekHeight = getBottomDistance(binding.shareButton)
        }
    }

    private fun getBottomDistance(view: View): Int {
        val position = IntArray(2)
        view.getLocationOnScreen(position)
        val metrics = resources.displayMetrics
        val totalHeight = metrics.heightPixels
        return totalHeight - (position[1] + view.height)
    }

    private fun showDialogDeleteTrack(idTrack: Long, idPlaylist: Long) {
            confirmDialog  = MaterialAlertDialogBuilder(requireContext(), R.style.dialogView)
                .setTitle(R.string.do_you_wanna_delete_track)
                .setMessage("")
                .setPositiveButton(R.string.yes) { _, _ ->
                    viewModel.deleteTrack(idTrack, idPlaylist)
                }
                .setNegativeButton(R.string.no) { _, _ ->
                    Log.i("clickToNo", "no")
                }
            confirmDialog.show()
    }

    private fun showDialogDeletePlaylist() {
        confirmDialog  = MaterialAlertDialogBuilder(requireContext(), R.style.dialogView)
            .setTitle(getString(R.string.do_you_wanna_delete_playlist, currentPlaylist.name))
            .setMessage("")
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deletePlaylist(currentPlaylist.id)
                findNavController().popBackStack()
            }
            .setNegativeButton(R.string.no) { _, _ ->
                Log.i("clickToNo", "no")
            }
        confirmDialog.show()
    }

    private fun buildMessage(): String {
        val formattedTrackList = tracks.withIndex().joinToString("\n") { (index, track) ->
            "${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTimeMillis})"
        }
        val messageBuilder = StringBuilder()
        messageBuilder.append("${currentPlaylist.name}\n")
        if (currentPlaylist.description != "") {
            messageBuilder.append("${currentPlaylist.description}\n")
        }
        messageBuilder.append("${currentPlaylist.count}\n")
        messageBuilder.append(formattedTrackList)
        return messageBuilder.toString()
    }


    private fun showContent(tracks: List<Track>) {
        binding.tracksList.isVisible = true
        trackAdapter?.tracks?.clear()
        trackAdapter?.tracks?.addAll(tracks)
        trackAdapter?.notifyDataSetChanged()
    }

    private fun showEmpty() {
        trackAdapter?.tracks?.clear()
        trackAdapter?.notifyDataSetChanged()
    }

    private fun tracksRender(state: ScreenState<out List<Track>>) {
        when (state) {
            is ScreenState.Content -> showContent(state.data)
            is ScreenState.Empty -> showEmpty()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        trackAdapter = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
        private const val CURRENT_PLAYLIST = "current_playlist"

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(
                CURRENT_PLAYLIST to playlistId
            )
    }
}