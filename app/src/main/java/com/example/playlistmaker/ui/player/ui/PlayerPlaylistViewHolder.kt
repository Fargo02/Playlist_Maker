package com.example.playlistmaker.ui.player.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlayerPlaylistItemBinding
import com.example.playlistmaker.domain.playlist.model.Playlist

class PlayerPlaylistViewHolder(
    private val binding: PlayerPlaylistItemBinding,
    private val clickListener: PlayerPlaylistAdapter.PlaylistClickListener
): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Playlist) {
        binding.name.text = model.name
        when(model.count % 10) {
            1 -> binding.count.text = "${model.count} трек"
            2, 3, 4, -> binding.count.text = "${model.count} трека"
            else -> binding.count.text = "${model.count} треков"
        }
        Glide.with(binding.cover)
            .load(model.imageUri)
            .transform(
                CenterCrop(),
                RoundedCorners(itemView.context.resources.getDimensionPixelSize(R.dimen.mark_2dp))
            )
            .placeholder(R.drawable.track_placeholder)
            .into(binding.cover)

        itemView.setOnClickListener { clickListener.onPlaylistClick(model) }
    }
}