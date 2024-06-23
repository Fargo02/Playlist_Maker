package com.example.playlistmaker.ui.library

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlistmaker.databinding.ActivityMediaLibraryBinding
import com.example.playlistmaker.databinding.ActivitySearchBinding

class MediaLibraryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaLibraryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}