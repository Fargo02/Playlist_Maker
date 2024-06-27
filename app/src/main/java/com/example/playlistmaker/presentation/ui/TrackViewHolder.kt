package com.example.playlistmaker.presentation.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import java.util.Locale

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val photoMaker = PhotoMaker()
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
    }
}