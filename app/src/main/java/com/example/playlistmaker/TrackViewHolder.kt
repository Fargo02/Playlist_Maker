package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private val cover: ImageView = itemView.findViewById(R.id.itemTrackCover)
    private val name: TextView = itemView.findViewById(R.id.itemTrackName)
    private val artist: TextView = itemView.findViewById(R.id.itemArtistName)
    private val time: TextView = itemView.findViewById(R.id.itemTrackTime)
    fun bind(model: Track) {
        name.text = model.trackName
        artist.text = model.artistName
        time.text = dateFormat.format(model.trackTimeMillis.toLong())
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(2))
            .fitCenter()
            .into(cover)
    }
}