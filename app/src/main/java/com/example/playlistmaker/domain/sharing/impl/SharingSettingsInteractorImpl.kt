package com.example.playlistmaker.domain.sharing.impl

import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.domain.sharing.settings.SharingSettingsInteractor
import com.example.playlistmaker.domain.sharing.settings.ExternalNavigator

class SharingSettingsInteractorImpl(
    private val externalNavigator: ExternalNavigator
): SharingSettingsInteractor {
    override fun shareApp(link : String) {
        externalNavigator.shareLink(link)
    }

    override fun openTerms(link : String) {
        externalNavigator.openLink(link)
    }

    override fun openSupport(email : EmailData) {
        externalNavigator.openEmail(email)
    }
}