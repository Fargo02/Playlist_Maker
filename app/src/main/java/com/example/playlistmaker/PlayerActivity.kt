package com.example.playlistmaker

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
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
    companion object{
        private const val DELAY = 400L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var currentTrack: Track
    private lateinit var timerThread: Runnable
    private var mainThreadHandler: Handler? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainThreadHandler = Handler(Looper.getMainLooper())

        val track = intent.getStringExtra(TRACK_INF)
        val type = object : TypeToken<Track>() {}.type
        currentTrack = Gson().fromJson(track, type) as Track

        if (currentTrack.previewUrl != null) {
            preparePlayer()
        }

        binding.buttonPlay.setOnClickListener {
            if (currentTrack.previewUrl != null) {
                playbackControl()
            } else {
                showMassage(resources.getString(R.string.playback_error))
            }
        }


        val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        val firstApiFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val date = LocalDate.parse(currentTrack.releaseDate , firstApiFormat)
        binding.trackName.text = currentTrack.trackName
        binding.artistName.text = currentTrack.artistName
        binding.playingTime.text = resources.getString(R.string.start_track)
        binding.trackTime.text = timeFormat.format(currentTrack.trackTimeMillis.toLong())
        binding.trackYear.text = date.year.toString()
        binding.trackGenre.text = currentTrack.primaryGenreName
        binding.trackCountry.text = currentTrack.country

        if (currentTrack.collectionName == null) {
            binding.albumGroup!!.isVisible = false
        } else {
            binding.albumName.text = currentTrack.collectionName
        }

        binding.buttonBack.setNavigationOnClickListener {
            finish()
        }

        Glide.with(binding.cover)
            .load(currentTrack.getCoverArtwork())
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
    }

    private fun startTime(){
        timerThread = createUpdateTimerTask()
        mainThreadHandler?.post(
            timerThread
        )
    }
    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                val currentTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                binding.playingTime.text = currentTime
                mainThreadHandler?.postDelayed(this, DELAY)
                Log.i("currentTime","currentTime: $currentTime")
            }
        }
    }
    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler?.removeCallbacks(timerThread)
        mediaPlayer.release()
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startTime()
                startPlayer()
            }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(currentTrack.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.buttonPlay.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            binding.buttonPlay.setBackgroundResource(R.drawable.button_play_off)
            binding.playingTime.text = resources.getString(R.string.start_track)
            mainThreadHandler?.removeCallbacks(timerThread)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.buttonPlay.setBackgroundResource(R.drawable.button_play_on)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        mainThreadHandler?.removeCallbacks(timerThread)
        binding.buttonPlay.setBackgroundResource(R.drawable.button_play_off)
        playerState = STATE_PAUSED
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
    private fun showMassage(text : String) {
        Toast.makeText(this, text,Toast.LENGTH_SHORT).show()
    }
}