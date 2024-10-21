package com.example.playlistmaker.data.sharing

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.createChooser
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.domain.sharing.settings.ExternalNavigator

class ExternalNavigatorImpl(
    private val context: Context
) : ExternalNavigator {

    override fun share(message: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        val chooserIntent = createChooser(shareIntent, "Поделиться").apply {
            addFlags(FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            startActivity(context, chooserIntent, Bundle())
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context,"Нет подходящего приложения", Toast.LENGTH_SHORT).show()
        }
    }

    override fun openLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(link)
            addFlags(FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(context, intent, Bundle())
    }

    override fun openEmail(emailData: EmailData) {
        val shareIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.address))
            putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
            putExtra(Intent.EXTRA_TEXT, emailData.text)
            addFlags(FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            startActivity(context, shareIntent, Bundle())
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context,"Нет подходящего приложения", Toast.LENGTH_SHORT).show()
        }
    }
}