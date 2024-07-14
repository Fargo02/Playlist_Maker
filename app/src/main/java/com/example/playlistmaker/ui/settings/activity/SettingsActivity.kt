package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.data.settings.App
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel

const val KEY_NIGHT_MODE = "nightMode"
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(KEY_NIGHT_MODE, MODE_PRIVATE)

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
            sharedPreferences.edit()
                .putBoolean(KEY_NIGHT_MODE, isChecked)
                .apply()
        }

        viewModel.observeChangeTheme().observe(this) {
            binding.themeSwitcher.isChecked = it
            Log.i("theme", "$it")
        }

        binding.settingsBack.setNavigationOnClickListener {
            finish()
        }

        binding.shareTheApp.setOnClickListener {
            viewModel.sharingSettingsInteractor.shareApp()
        }

        binding.writeToSupport.setOnClickListener {
            viewModel.sharingSettingsInteractor.openSupport()

        }

        binding.userAgreement.setOnClickListener {
            viewModel.sharingSettingsInteractor.openTerms()
        }
    }
}