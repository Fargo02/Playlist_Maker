package com.example.playlistmaker.ui.library.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.domain.playlist.model.Playlist

class PlaylistViewHolder(
    private val binding: PlaylistItemBinding,
    private val clickListener: PlaylistAdapter.TrackClickListener
): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Playlist) {
        binding.name.text = model.name
        when(model.count % 10) {
            1 -> binding.count.text = "${model.count} трек"
            2, 3, 4, -> binding.count.text = "${model.count} трека"
            else -> binding.count.text = "${model.count} треков"
        }
        Glide.with(itemView)
            .load(model.imageUri)
            .transform(CenterCrop(), RoundedCorners(8))
            .placeholder(R.drawable.playlist_placeholder)
            .into(binding.cover)

        itemView.setOnClickListener { clickListener.onPlaylistClick(playlist = model) }
    }

}