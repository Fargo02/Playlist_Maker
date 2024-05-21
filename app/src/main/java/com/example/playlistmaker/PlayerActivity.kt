package com.example.playlistmaker

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.SearchActivity.Companion.TRACK_INF
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var trackNow: Track

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = intent.getStringExtra(TRACK_INF)
        val type = object : TypeToken<Track>() {}.type
        trackNow = Gson().fromJson(track, type) as Track

        val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        val firstApiFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val date = LocalDate.parse(trackNow.releaseDate , firstApiFormat)

        binding.trackName.text = trackNow.trackName
        binding.artistName.text = trackNow.artistName
        binding.playingTime.text = timeFormat.format(trackNow.trackTimeMillis.toLong())
        binding.trackTime.text = timeFormat.format(trackNow.trackTimeMillis.toLong())
        binding.trackYear.text = date.year.toString()
        binding.trackGenre.text = trackNow.primaryGenreName
        binding.trackCountry.text = trackNow.country

        if (trackNow.collectionName == null) {
            binding.albumGroup!!.isVisible = false
        } else {
            binding.albumName.text = trackNow.collectionName
        }

        binding.buttonBack.setNavigationOnClickListener {
            finish()
        }

        Glide.with(binding.cover)
            .load(trackNow.getCoverArtwork())
            .placeholder(R.drawable.big_placeholder)
            .fitCenter()
            .transform(RoundedCorners(8))
            .into(binding.cover)

        binding.buttonAddToList.setOnClickListener {
            buttonEffect(binding.buttonAddToList)
            if (binding.buttonAddToList.background.constantState == resources.getDrawable(R.drawable.button_add_to_list_off).constantState) {
                binding.buttonAddToList.setBackgroundResource(R.drawable.button_add_to_list_on)
            } else {
                binding.buttonAddToList.setBackgroundResource(R.drawable.button_add_to_list_off)
            }
        }
        binding.buttonLike.setOnClickListener {
            buttonEffect(binding.buttonLike)
            if (binding.buttonLike.background.constantState == resources.getDrawable(R.drawable.button_like_off).constantState) {
                binding.buttonLike.setBackgroundResource(R.drawable.button_like_on)
            } else {
                binding.buttonLike.setBackgroundResource(R.drawable.button_like_off)
            }
        }
        binding.buttonPlay.setOnClickListener {
            buttonEffect(binding.buttonPlay)
            if (binding.buttonPlay.background.constantState == resources.getDrawable(R.drawable.button_play_off).constantState) {
                binding.buttonPlay.setBackgroundResource(R.drawable.button_play_on)
            } else {
                binding.buttonPlay.setBackgroundResource(R.drawable.button_play_off)
            }
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun buttonEffect(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(resources.getColor(R.color.color_control_highlight), PorterDuff.Mode.SRC_ATOP)
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }
    }
}