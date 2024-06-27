package com.example.playlistmaker.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackAdapter: RecyclerView.Adapter<TrackViewHolder> () {
    var tracks = ArrayList<Track>()
    var itemClickListener: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(tracks[position])
        }
    }
    override fun getItemCount(): Int {
        return tracks.size
    }
    fun setOnItemClickListener(listener: (Track) -> Unit) {
        itemClickListener = listener
    }
}