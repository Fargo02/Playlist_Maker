package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("CutPasteId", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsBack = findViewById<Toolbar>(R.id.settingsBack)
        val share = findViewById<FrameLayout>(R.id.shareTheApp)
        val writeToSupport = findViewById<FrameLayout>(R.id.writeToSupport)
        val userAgreement = findViewById<FrameLayout>(R.id.userAgreement)

        settingsBack.setNavigationOnClickListener {
            finish()
        }

        share.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.link_to_the_practicum))
            startActivity(Intent.createChooser(shareIntent, "Поделиться"))
        }

        writeToSupport.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail)))
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.title_message_for_developer))
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message_for_developer))
            startActivity(shareIntent)
        }

        userAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.link_to_the_agreement))
            startActivity(intent)
        }
    }
}