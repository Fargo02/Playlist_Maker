package com.example.playlistmaker.ui.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.search.model.Track

class TrackViewHolder(
    itemView: View,
    private val clickListener: TrackAdapter.TrackClickListener
): RecyclerView.ViewHolder(itemView) {
    private val photoMaker = ImageMaker()
    private val cover: ImageView = itemView.findViewById(R.id.itemTrackCover)
    private val name: TextView = itemView.findViewById(R.id.itemTrackName)
    private val artist: TextView = itemView.findViewById(R.id.itemArtistName)
    private val time: TextView = itemView.findViewById(R.id.itemTrackTime)
    fun bind(model: Track) {
        name.text = model.trackName
        artist.text = model.artistName
        time.text = model.trackTimeMillis
        photoMaker.getPhoto(
            cover,
            model.artworkUrl100,
            R.drawable.placeholder,
            2
        )

        itemView.setOnClickListener { clickListener.onTrackClick(track = model) }
    }
}