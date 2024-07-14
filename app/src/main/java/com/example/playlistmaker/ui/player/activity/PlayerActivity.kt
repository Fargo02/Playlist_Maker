package com.example.playlistmaker.ui.player.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.search.activity.SearchActivity.Companion.TRACK_INF
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.player.PlayerState
import com.example.playlistmaker.domain.player.PlayerState.*
import com.example.playlistmaker.ui.mapper.ArtworkMapper
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.ui.ImageMaker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlayerActivity : AppCompatActivity() {

    companion object{
        private const val DELAY = 400L
    }

    private val imageMaker = ImageMaker()

    private lateinit var binding: ActivityPlayerBinding

    private lateinit var currentTrack: Track

    private var timerThread: Runnable? = null
    private var mainThreadHandler: Handler? = null

    private val viewModel by viewModels<PlayerViewModel>{
        PlayerViewModel.factory(currentTrack.previewUrl!!)
    }

    private lateinit var playerState: PlayerState

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainThreadHandler = Handler(Looper.getMainLooper())

        binding.buttonPlay.isEnabled = false

        val track = intent.getStringExtra(TRACK_INF)
        val type = object : TypeToken<Track>() {}.type
        currentTrack = Gson().fromJson(track, type) as Track

        imageMaker.getPhoto(
            binding.cover,
            ArtworkMapper.getCoverArtwork(currentTrack.artworkUrl100),
            R.drawable.big_placeholder,
            8
        )

        viewModel.observeStateListener().observe(this) { state ->
            when (state!!) {
                PREPARED -> {
                    playerState = PAUSED
                    binding.buttonPlay.isEnabled = true
                    binding.playingTime.text = resources.getString(R.string.start_track)
                    timerThread?.let { mainThreadHandler?.removeCallbacks(it) }
                    startTime()
                    binding.buttonPlay.setBackgroundResource(R.drawable.button_play_on)
                }
                PLAYING -> {
                    playerState = PAUSED
                    startTime()
                    binding.buttonPlay.setBackgroundResource(R.drawable.button_play_on)
                }
                PAUSED -> {
                    playerState = PLAYING
                    timerThread?.let { mainThreadHandler?.removeCallbacks(it) }
                    binding.buttonPlay.setBackgroundResource(R.drawable.button_play_off)
                }
                DEFAULT -> {
                    playerState = PREPARED
                    binding.buttonPlay.isEnabled = true
                    timerThread?.let { mainThreadHandler?.removeCallbacks(it) }
                    binding.buttonPlay.setBackgroundResource(R.drawable.button_play_off)
                    binding.playingTime.text = resources.getString(R.string.start_track)
                }
            }
        }

        binding.buttonPlay.setOnClickListener {
            viewModel.listener.onChange(playerState)
        }

        binding.trackName.text = currentTrack.trackName
        binding.artistName.text = currentTrack.artistName
        binding.playingTime.text = resources.getString(R.string.start_track)
        binding.trackTime.text = currentTrack.trackTimeMillis
        binding.trackYear.text = currentTrack.releaseDate
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

        binding.buttonAddToList.setOnClickListener {
            if (binding.buttonAddToList.background.constantState == resources.getDrawable(R.drawable.button_add_to_list_off).constantState) {
                binding.buttonAddToList.setBackgroundResource(R.drawable.button_add_to_list_on)
            } else {
                binding.buttonAddToList.setBackgroundResource(R.drawable.button_add_to_list_off)
            }
        }

        binding.buttonLike.setOnClickListener {
            if (binding.buttonLike.background.constantState == resources.getDrawable(R.drawable.button_like_off).constantState) {
                binding.buttonLike.setBackgroundResource(R.drawable.button_like_on)
            } else {
                binding.buttonLike.setBackgroundResource(R.drawable.button_like_off)
            }
        }
    }

    private fun startTime(){
        timerThread = createUpdateTimerTask()
        timerThread?.let { mainThreadHandler?.post(it) }
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                val currentTime = viewModel.playerInteractor.getCurrentTime()
                binding.playingTime.text = currentTime
                mainThreadHandler?.postDelayed(this, DELAY)
                Log.i("currentTime","currentTime: $currentTime")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerThread?.let { mainThreadHandler?.removeCallbacks(it) }
        viewModel.playerInteractor.release()
    }

    override fun onPause() {
        super.onPause()
        playerState = PAUSED
        viewModel.listener.onChange(playerState)
    }
}