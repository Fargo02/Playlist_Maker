package com.example.playlistmaker

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class Player : AppCompatActivity() {
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var playingTime: TextView
    private lateinit var trackDuration: TextView
    private lateinit var albumName: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView
    private lateinit var cover: ImageView
    private lateinit var buttonBack: Toolbar
    private lateinit var buttonPlay: ImageButton
    private lateinit var buttonAddTrack: ImageButton
    private lateinit var buttonLike: ImageButton
    private lateinit var albumGroup: Group

    private lateinit var trackNow: Track

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val track = intent.getStringExtra("trackInf")
        val type = object : TypeToken<Track>() {}.type
        trackNow = Gson().fromJson(track, type) as Track

        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        playingTime = findViewById(R.id.playingTime)
        trackDuration = findViewById(R.id.trackTime)
        albumName = findViewById(R.id.albumName)
        trackYear = findViewById(R.id.trackYear)
        trackGenre = findViewById(R.id.trackGenre)
        trackCountry = findViewById(R.id.trackCountry)
        cover = findViewById(R.id.cover)
        buttonBack = findViewById(R.id.buttonBack)
        buttonPlay = findViewById(R.id.buttonPlay)
        buttonAddTrack = findViewById(R.id.buttonAddToList)
        buttonLike = findViewById(R.id.buttonLike)
        albumGroup = findViewById(R.id.albumGroup)

        val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        val firstApiFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val date = LocalDate.parse(trackNow.releaseDate , firstApiFormat)

        trackName.text = trackNow.trackName
        artistName.text = trackNow.artistName
        playingTime.text = timeFormat.format(trackNow.trackTimeMillis.toLong())
        trackDuration.text = timeFormat.format(trackNow.trackTimeMillis.toLong())
        trackYear.text = date.year.toString()
        trackGenre.text = trackNow.primaryGenreName
        trackCountry.text = trackNow.country

        if (trackNow.collectionName == null) {
            albumGroup.isVisible = false
        } else {
            albumName.text = trackNow.collectionName
        }

        buttonBack.setNavigationOnClickListener {
            finish()
        }

        Glide.with(cover)
            .load(trackNow.getCoverArtwork())
            .placeholder(R.drawable.big_placeholder)
            .fitCenter()
            .transform(RoundedCorners(8))
            .into(cover)

        buttonAddTrack.setOnClickListener {
            buttonEffect(buttonAddTrack)
            if (buttonAddTrack.background.constantState == resources.getDrawable(R.drawable.button_add_to_list_off).constantState) {
                buttonAddTrack.setBackgroundResource(R.drawable.button_add_to_list_on)
            } else {
                buttonAddTrack.setBackgroundResource(R.drawable.button_add_to_list_off)
            }
        }
        buttonLike.setOnClickListener {
            buttonEffect(buttonLike)
            if (buttonLike.background.constantState == resources.getDrawable(R.drawable.button_like_off).constantState) {
                buttonLike.setBackgroundResource(R.drawable.button_like_on)
            } else {
                buttonLike.setBackgroundResource(R.drawable.button_like_off)
            }
        }
        /*
        buttonPlay.setOnClickListener {
            buttonEffect(buttonPlay)
            if (buttonPlay.background.constantState == resources.getDrawable(R.drawable.button_play_off).constantState) {
                buttonPlay.setBackgroundResource(R.drawable.button_play_on)
            } else {
                buttonPlay.setBackgroundResource(R.drawable.button_play_off)
            }
        }
         */
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