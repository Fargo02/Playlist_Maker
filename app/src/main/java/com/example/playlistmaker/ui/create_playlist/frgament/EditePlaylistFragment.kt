package com.example.playlistmaker.ui.create_playlist.frgament

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.ui.create_playlist.view_model.CreatePlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditePlaylistFragment(): CreatePlaylistFragment() {

    private lateinit var currentPlaylist : Playlist

    private var playlistId: Long = 0

    private var trackList = ""

    override val viewModel: CreatePlaylistViewModel by viewModel {
        parametersOf(playlistId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistId = requireArguments().getLong(CURRENT_PLAYLIST)

        viewModel.getPlaylist()

        viewModel.observePlaylistListener().observe(viewLifecycleOwner) { playlist ->
            currentPlaylist = playlist
            coverPlaylist = playlist.imageUri
            trackList = playlist.trackList

            binding.inputEditTextName.setText(playlist.name)
            binding.inputEditTextDescription.setText(playlist.description)

            if (playlist.imageUri != "") {
                binding.imageCoverPlaceholder.isVisible = false
                Glide.with(binding.cover)
                    .load(playlist.imageUri)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(resources.getDimensionPixelSize(R.dimen.mark_8dp))
                    )
                    .into(binding.imageCover)
            }
        }

        binding.toolbar.title = getString(R.string.edite)
        binding.bottomCreate.text = getString(R.string.save)
        binding.bottomCreate.setOnClickListener {
            createPlaylist()
        }
    }

    override fun createPlaylist() {
        viewModel.updatePlaylist(
            Playlist(
            id = currentPlaylist.id,
            name = namePlaylist,
            description = descriptionPlaylist,
            imageUri = coverPlaylist,
            trackList = trackList,
            count = if(trackList == "") 0 else trackList.split(",").count()
            )
        )
        findNavController().popBackStack()
    }

    override fun showConfirmDialog() { }

    companion object {
        private const val CURRENT_PLAYLIST = "current_playlist"

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(
                CURRENT_PLAYLIST to playlistId
            )
    }
}