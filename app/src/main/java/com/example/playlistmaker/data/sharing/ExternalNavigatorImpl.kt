package com.example.playlistmaker.data.sharing

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.domain.sharing.settings.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareLink(link: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        startActivity(context, Intent.createChooser(shareIntent, "Поделиться"), Bundle())
    }

    override fun openLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(link)
        startActivity(context, intent, Bundle())
    }

    override fun openEmail(emailData: EmailData) {
        val shareIntent = Intent(Intent.ACTION_SENDTO)
        shareIntent.data = Uri.parse("mailto:")
        shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.address))
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
        shareIntent.putExtra(Intent.EXTRA_TEXT, emailData.text)
        try {
            startActivity(context, shareIntent, Bundle())
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context,"Нет подходящего приложения", Toast.LENGTH_SHORT).show()
        }
    }
}