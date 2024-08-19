package com.example.playlistmaker.ui.player.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.player.PlayerState
import com.example.playlistmaker.domain.player.PlayerState.DEFAULT
import com.example.playlistmaker.domain.player.PlayerState.PAUSED
import com.example.playlistmaker.domain.player.PlayerState.PLAYING
import com.example.playlistmaker.domain.player.PlayerState.PREPARED
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.mapper.ArtworkMapper
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.ui.ImageMaker
import com.example.playlistmaker.utils.BindingFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment(): BindingFragment<FragmentPlayerBinding>() {

    private val imageMaker = ImageMaker()

    private lateinit var currentTrack: Track

    private var timerThread: Runnable? = null
    private var mainThreadHandler: Handler? = null

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(currentTrack.previewUrl)
    }

    private lateinit var playerState: PlayerState

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = requireArguments().getString(CURRENT_TRACK) ?: ""
        val type = object : TypeToken<Track>() {}.type
        currentTrack = Gson().fromJson(track, type) as Track

        mainThreadHandler = Handler(Looper.getMainLooper())

        binding.buttonPlay.isEnabled = false

        imageMaker.getPhoto(
            binding.cover,
            ArtworkMapper.getCoverArtwork(currentTrack.artworkUrl100),
            R.drawable.big_placeholder,
            8
        )

        viewModel.observeStateListener().observe(viewLifecycleOwner) { state ->
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

        binding.buttonBack.setNavigationOnClickListener {
            findNavController().popBackStack(R.id.searchFragment, false)
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
                val currentTime = viewModel.getCurrentTime()
                binding.playingTime.text = currentTime
                mainThreadHandler?.postDelayed(this, DELAY)
                Log.i("currentTime","currentTime: $currentTime")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timerThread?.let { mainThreadHandler?.removeCallbacks(it) }
        viewModel.getRelease()
    }

    override fun onPause() {
        super.onPause()
        playerState = PAUSED
        viewModel.listener.onChange(playerState)
    }

    companion object{

        private const val DELAY = 400L
        private const val CURRENT_TRACK = "current_track"

        fun createArgs(track: String): Bundle =
            bundleOf(
            CURRENT_TRACK to track
        )
    }

}