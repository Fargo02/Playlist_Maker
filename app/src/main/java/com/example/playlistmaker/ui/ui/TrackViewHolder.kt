package com.example.playlistmaker.ui.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.domain.search.model.Track

class TrackViewHolder(
    private val binding: TrackItemBinding,
    private val clickListener: TrackAdapter.TrackClickListener,
): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Track) {
        binding.itemTrackName.text = model.trackName
        binding.itemArtistName.text = model.artistName
        binding.itemTrackTime.text = model.trackTimeMillis

        Glide.with(binding.itemTrackCover)
            .load(model.artworkUrl100)
            .transform(
                CenterCrop(),
                RoundedCorners(itemView.context.resources.getDimensionPixelSize(R.dimen.mark_2dp))
            )
            .placeholder(R.drawable.track_placeholder)
            .into(binding.itemTrackCover)

        itemView.setOnClickListener { clickListener.onTrackClick(model) }
        itemView.setOnLongClickListener {
            clickListener.onLongClickListener(track = model)
            return@setOnLongClickListener true
        }
    }
}