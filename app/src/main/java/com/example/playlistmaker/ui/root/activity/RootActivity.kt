package com.example.playlistmaker.ui.root.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener{ _, destination, _ ->
            when(destination.id) {
                R.id.playerFragment -> {
                    binding.bottomNavigationView.isVisible = false
                    binding.divider.isVisible = false
                }
                R.id.fragmentCreatePlaylist -> {
                    binding.bottomNavigationView.isVisible = false
                    binding.divider.isVisible = false
                }
                else -> {
                    binding.bottomNavigationView.isVisible = true
                    binding.divider.isVisible = true
                }
            }
        }

    }
}
