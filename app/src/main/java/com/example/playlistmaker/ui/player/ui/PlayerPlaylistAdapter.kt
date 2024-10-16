package com.example.playlistmaker.ui.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlayerPlaylistItemBinding
import com.example.playlistmaker.domain.playlist.model.Playlist

class PlayerPlaylistAdapter(
    private val clickListener: PlaylistClickListener
): RecyclerView.Adapter<PlayerPlaylistViewHolder>() {

    var playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerPlaylistViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlayerPlaylistViewHolder(PlayerPlaylistItemBinding.inflate(layoutInspector, parent, false), clickListener)
    }

    override fun onBindViewHolder(holder: PlayerPlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun interface PlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }
}