package com.example.playlistmaker.ui.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.library.ui.PlaylistViewHolder

class TrackAdapter(private val clickListener: TrackClickListener): RecyclerView.Adapter<TrackViewHolder> () {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return TrackViewHolder(TrackItemBinding.inflate(layoutInspector, parent, false), clickListener)
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