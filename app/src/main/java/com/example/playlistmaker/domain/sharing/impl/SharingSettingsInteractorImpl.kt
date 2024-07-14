package com.example.playlistmaker.domain.sharing.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.domain.sharing.settings.SharingSettingsInteractor
import com.example.playlistmaker.domain.sharing.settings.ExternalNavigator

class SharingSettingsInteractorImpl(
    private val context: Context,
    private val externalNavigator: ExternalNavigator
): SharingSettingsInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.link_to_the_practicum)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            address = context.getString(R.string.mail),
            subject = context.getString(R.string.title_message_for_developer),
            text = context.getString(R.string.message_for_developer)
        )
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.link_to_the_agreement)
    }
}