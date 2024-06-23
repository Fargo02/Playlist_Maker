package com.example.playlistmaker.ui.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    @SuppressLint("CutPasteId", "MissingInflatedId", "UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.themeSwitcher.isChecked = (applicationContext as App).darkTheme
        binding.themeSwitcher.setOnCheckedChangeListener { switcher, isChecked ->
            (applicationContext as App).switchTheme(isChecked)

        }

        binding.settingsBack.setNavigationOnClickListener {
            finish()
        }

        binding.shareTheApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.link_to_the_practicum))
            startActivity(Intent.createChooser(shareIntent, "Поделиться"))
        }

        binding.writeToSupport.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail)))
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.title_message_for_developer))
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message_for_developer))
            startActivity(shareIntent)
        }

        binding.userAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.link_to_the_agreement))
            startActivity(intent)
        }
    }
}