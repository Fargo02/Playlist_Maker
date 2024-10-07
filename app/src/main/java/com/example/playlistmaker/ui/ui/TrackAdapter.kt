package com.example.playlistmaker.ui.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.search.model.Track

class TrackAdapter(private val clickListener: TrackClickListener): RecyclerView.Adapter<TrackViewHolder> () {
    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view, clickListener)
    }
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }
    override fun getItemCount(): Int {
        return tracks.size
    }
    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }
}