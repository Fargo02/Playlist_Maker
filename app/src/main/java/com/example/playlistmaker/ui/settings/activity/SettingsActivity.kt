package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.example.playlistmaker.utils.Application
import org.koin.androidx.viewmodel.ext.android.viewModel

const val KEY_NIGHT_MODE = "nightMode"
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(KEY_NIGHT_MODE, MODE_PRIVATE)

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as Application).switchTheme(isChecked)
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
            viewModel.getShareTheApp(getShareAppLink())
        }

        binding.writeToSupport.setOnClickListener {
            viewModel.getWriteToSupport(getSupportEmailData())

        }

        binding.userAgreement.setOnClickListener {
            viewModel.getUserAgreement(getTermsLink())
        }
    }
    private fun getShareAppLink(): String {
        return getString(R.string.link_to_the_practicum)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            address = getString(R.string.mail),
            subject = getString(R.string.title_message_for_developer),
            text = getString(R.string.message_for_developer)
        )
    }

    private fun getTermsLink(): String {
        return getString(R.string.link_to_the_agreement)
    }
}